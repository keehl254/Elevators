package me.keehl.elevators.menus.admin;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ColorHelper;
import me.keehl.elevators.helpers.ElevatorMenuHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorHookService;
import me.keehl.elevators.services.ElevatorTypeService;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigRecipe;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import me.keehl.elevators.util.config.RecipeRow;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class AdminEditElevatorRecipeMenu {

    private static void saveInventoryToRecipe(Inventory inventory, ElevatorRecipeGroup tempRecipe) {
        List<RecipeRow<NamespacedKey>> keyList = new ArrayList<>();
        for (int y = 0; y < 3; y++) {

            RecipeRow<NamespacedKey> keyRow = new RecipeRow<>();
            for (int x = 0; x < 3; x++) {
                int slot = 10 + (y * 9) + x;
                ItemStack item = inventory.getItem(slot);
                NamespacedKey key = (item == null || item.getType().isAir()) ? Material.AIR.getKey() : ElevatorHookService.getKeyFromItemStack(item);
                keyRow.add(key);
            }
            keyList.add(keyRow);
        }

        ConfigRecipe.setRecipe(tempRecipe, keyList);
    }

    private static void runRecipeColorTask(ElevatorType elevatorType, Inventory inventory, ElevatorRecipeGroup tempRecipe, AtomicInteger dyeColorIndex) {
        DyeColor color = null;
        if (!tempRecipe.supportsMultiColorOutput()) {
            color = tempRecipe.getDefaultOutputColor();
        } else if (!tempRecipe.supportsMultiColorMaterials()) {

            for (List<NamespacedKey> keyRow : tempRecipe.getRecipe()) {
                for (NamespacedKey key : keyRow) {
                    boolean colorable = key.getNamespace().equalsIgnoreCase(NamespacedKey.MINECRAFT) || key.getNamespace().equalsIgnoreCase(Elevators.getInstance().getName().toLowerCase(Locale.ROOT));
                    if (!colorable)
                        continue;

                    ItemStack item = ElevatorHookService.createItemStackFromKey(key);
                    if (item == null)
                        continue;

                    DyeColor tempColor = ItemStackHelper.getDyeColorFromMaterial(item.getType());
                    if (tempColor == null)
                        continue;

                    if (color != null && tempColor != color) {
                        color = tempRecipe.getDefaultOutputColor(); // Multiple colorable materials in recipe. Cannot determine output color.
                        break;
                    }
                    color = tempColor;
                }
            }

        } else
            color = DyeColor.values()[dyeColorIndex.get()];

        ItemStack elevatorItemStack = ItemStackHelper.createItemStackFromElevatorType(elevatorType, color);
        elevatorItemStack.setAmount(tempRecipe.getAmount());

        inventory.setItem(25, elevatorItemStack);
        dyeColorIndex.set(dyeColorIndex.incrementAndGet() % DyeColor.values().length);
    }

    // Elevators most complicated menu.
    public static void openAdminEditElevatorRecipeMenu(Player player, ElevatorType tempElevatorType, ElevatorRecipeGroup currentRecipeGroup) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 54, "Settings > Recipes > Recipe");

        ElevatorRecipeGroup tempRecipe = new ElevatorRecipeGroup();
        if (currentRecipeGroup != null) {
            ConfigRecipe.setAmount(tempRecipe, currentRecipeGroup.amount);
            ConfigRecipe.setCraftPermission(tempRecipe, currentRecipeGroup.getCraftPermission());
            ConfigRecipe.setDefaultOutputColor(tempRecipe, currentRecipeGroup.getDefaultOutputColor());
            ConfigRecipe.setMultiColorOutput(tempRecipe, currentRecipeGroup.supportMultiColorOutput());
            ConfigRecipe.setMultiColorMaterials(tempRecipe, currentRecipeGroup.supportMultiColorMaterials());
            ConfigRecipe.setRecipe(tempRecipe, currentRecipeGroup.getRecipe());
            tempRecipe.setKey(currentRecipeGroup.getRecipeKey());
        }

        AtomicInteger dyeColorIndex = new AtomicInteger(0);
        WrappedTask colorTask = Elevators.getFoliaLib().getScheduler().runTimer(() -> runRecipeColorTask(elevatorType, inventory, tempRecipe, dyeColorIndex), 20, 20);

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> {
            colorTask.cancel();
            AdminEditRecipesMenu.openAdminEditRecipesMenu(player, elevatorType);
        }, SimpleDisplay.DisplayClickResult.CANCEL, SimpleDisplay.DisplayClickResult.ALLOW);
        ElevatorMenuHelper.fillEmptySlotsWithPanes(inventory, DyeColor.BLACK);

        int x = 0;
        int y = 0;
        for (List<NamespacedKey> keyRow : tempRecipe.getRecipe()) {
            for (NamespacedKey key : keyRow) {
                ItemStack item = ElevatorHookService.createItemStackFromKey(key);
                if (item == null)
                    item = new ItemStack(Material.AIR, 1);

                int slot = 10 + (y * 9) + (x % 3);
                BiFunction<InventoryClickEvent, SimpleDisplay, SimpleDisplay.DisplayClickResult> onClick = (event, myDisplay) -> SimpleDisplay.DisplayClickResult.ALLOW;
                display.setItem(slot, item, onClick);
                x++;
            }
            y++;
        }

        ItemStack permissionTemplate = ItemStackHelper.createItem(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Craft Permission", Material.CHAIN_COMMAND_BLOCK, 1, Arrays.asList("",
                ChatColor.GRAY + "Current Value: ",
                ChatColor.GOLD + tempRecipe.getCraftPermission())
        );
        ItemStack outputColorTemplate = ItemStackHelper.createItem(
                ColorHelper.getChatStringFromColor(tempRecipe.getDefaultOutputColor().getColor().asRGB()) + ChatColor.BOLD + "Change Default Output Color",
                ItemStackHelper.getVariant(Material.BLACK_DYE, tempRecipe.getDefaultOutputColor()),
                1
        );
        ItemStack multiColorMaterialTemplate = ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Multi-Color Materials", Material.FIREWORK_STAR, 1);
        ItemStack multiColorOutputTemplate = ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Multi-Color Output", Material.FIREWORK_ROCKET, 1);

        ItemStack amountTemplate = ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Quantity", Material.EXPERIENCE_BOTTLE, 1);
        Function<Integer, ItemStack> createAmountIcon = amount -> ElevatorMenuHelper.createValueButton(amountTemplate, amount, Object::toString, ElevatorMenuHelper.createActionMap(Arrays.asList("Left Click", "Right Click"), Arrays.asList("Increase Quantity", "Decrease Quantity")));
        Supplier<ItemStack> createMultiColorMaterialIcon = () -> ElevatorMenuHelper.createBooleanButton(multiColorMaterialTemplate, tempRecipe.supportsMultiColorMaterials(), ElevatorMenuHelper.createActionMap(Collections.singletonList("Left Click"), Collections.singletonList("Toggle")));
        Supplier<ItemStack> createMultiColorOutputIcon = () -> ElevatorMenuHelper.createBooleanButton(multiColorOutputTemplate, tempRecipe.supportsMultiColorOutput(), ElevatorMenuHelper.createActionMap(Collections.singletonList("Left Click"), Collections.singletonList("Toggle")));

        display.setReturnButton(45, ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "CANCEL", Material.BARRIER, 1));
        display.setItemSimple(47, permissionTemplate, (event, myDisplay) -> {
            saveInventoryToRecipe(inventory, tempRecipe);
            colorTask.cancel();
            display.stopReturn();
            AdminEditRecipePermissionMenu.openEditRecipePermissionMenu(player, elevatorType, tempRecipe);
        });

        display.setItemSimple(48, createAmountIcon.apply(tempRecipe.getAmount()), (event, myDisplay) -> {
            int newValue = tempRecipe.getAmount() + (event.isLeftClick() ? 1 : -1);
            newValue = Math.min(Math.max(newValue, 1), elevatorType.getMaxStackSize());
            ConfigRecipe.setAmount(tempRecipe, newValue);
            inventory.setItem(48, createAmountIcon.apply(newValue));
        });

        display.setItemSimple(49, ElevatorMenuHelper.createValueButton(outputColorTemplate, tempRecipe.getDefaultOutputColor(), DyeColor::name, ElevatorMenuHelper.createActionMap(Collections.singletonList("Left Click"), Collections.singletonList("Change Color"))), (event, myDisplay) -> {
            saveInventoryToRecipe(inventory, tempRecipe);
            colorTask.cancel();
            display.stopReturn();
            ElevatorMenuHelper.openChooseDyeColorMenu(player, "Recipes > Recipe > Color", color -> {
                ConfigRecipe.setDefaultOutputColor(tempRecipe, color);

                openAdminEditElevatorRecipeMenu(player, elevatorType, tempRecipe);
            }, () -> openAdminEditElevatorRecipeMenu(player, elevatorType, tempRecipe));
        });

        display.setItemSimple(50, createMultiColorMaterialIcon.get(), (event, myDisplay) -> {
            ConfigRecipe.setMultiColorMaterials(tempRecipe, !tempRecipe.supportsMultiColorMaterials());
            inventory.setItem(50, createMultiColorMaterialIcon.get());
        });

        display.setItemSimple(51, createMultiColorOutputIcon.get(), (event, myDisplay) -> {
            ConfigRecipe.setMultiColorOutput(tempRecipe, !tempRecipe.supportsMultiColorOutput());
            inventory.setItem(51, createMultiColorOutputIcon.get());
        });

        display.setItemSimple(53, ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE", Material.ARROW, 1), (event, myDisplay) -> {
            saveInventoryToRecipe(inventory, tempRecipe);
            colorTask.cancel();
            display.stopReturn();
            AdminSaveElevatorRecipeMenu.openSaveElevatorRecipeMenu(player, elevatorType, tempRecipe);
        });

        display.open();
    }
}
