package me.keehl.elevators.helpers;

import me.keehl.dialogbuilder.DialogManager;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.*;
import me.keehl.elevators.services.*;
import me.keehl.elevators.services.interaction.PagedDisplay;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.*;
import java.util.stream.IntStream;

// Until Paper removes the String methods, I will keep this all in one class for both Spigot and Paper. Sorry deprecation checker.
@SuppressWarnings({"deprecation"})
public class ElevatorMenuHelper {

    private static DialogManager dialogManager;

    public static void openConfirmMenu(Player player, Consumer<Boolean> onConfirm) {

        Inventory inventory = Bukkit.createInventory(null, 54, "Are you sure?");

        BiConsumer<InventoryClickEvent, SimpleDisplay> confirmConsumer = (event, myDisplay) -> {
            if (event.getCurrentItem() == null)
                return;
            myDisplay.stopReturn();
            onConfirm.accept(event.getCurrentItem().getType() == Material.LIME_WOOL);
        };

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> onConfirm.accept(false));
        for (int i = 0; i < 54; i++)
            display.setItemSimple(i, (i % 9) < 4 ? ItemStackHelper.createItem("Accept", Material.LIME_WOOL, 1) : ((i % 9) > 4 ? ItemStackHelper.createItem("Deny", Material.RED_WOOL, 1) : null), confirmConsumer);

        display.open();
    }

    public static void openChooseDyeColorMenu(Player player, String title, Consumer<DyeColor> returnMethod, Runnable onCancel) {
        PagedDisplay<DyeColor> display = new PagedDisplay<>(Elevators.getInstance(), player, Arrays.asList(DyeColor.values()), title, onCancel);
        display.onCreateItem(dyeColor ->
                ItemStackHelper.createItem(ColorHelper.getChatStringFromColor(dyeColor.getColor().asRGB()) + ChatColor.BOLD + dyeColor.name(), ItemStackHelper.getVariant(Material.BLACK_DYE, dyeColor), 1)
        );
        display.onClick((dyeColor, event, myDisplay) -> {
            display.stopReturn();
            returnMethod.accept(dyeColor);
        });
        display.open();
    }

    public static Map<String, String> createActionMap(List<String> keyList, List<String> actionList) {
        Map<String, String> actions = new HashMap<>();
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            if (actionList.size() > i)
                actions.put(key, actionList.get(i));
        }

        return actions;
    }

    public static <T> Map<T, Integer> mapToInventorySlot(List<T> objects) {
        Map<T, Integer> objectMap = new HashMap<>();

        int inventorySize = objects.size() + (9 - (objects.size()) % 9);

        int numRows = (inventorySize / 9);
        int currentItemIndex = 0;
        for (int row = 0; row < numRows && currentItemIndex < objects.size(); row++) {
            int itemsToPlace = Math.min(9, objects.size() - currentItemIndex);

            int startColIndex = Math.max((9 - itemsToPlace) / 2, 0);

            for (int col = startColIndex; col < startColIndex + itemsToPlace; col++) {
                int slot = ((row + 1) * 9) + col;
                objectMap.put(objects.get(currentItemIndex), slot);
                currentItemIndex++;
            }
        }

        return objectMap;
    }

    public static <T> ItemStack createValueButton(ItemStack template, T value, Function<T, String> serializeMethod, Map<String, String> actions) {
        List<String> lore = new ArrayList<>();

        ItemMeta templateMeta = template.getItemMeta();
        if (templateMeta.hasLore())
            lore.addAll(Objects.requireNonNull(templateMeta.getLore()));

        lore.add("");
        lore.add(ChatColor.GRAY + "Current Value: ");
        if (value instanceof Boolean boolValue) {
            lore.add(boolValue ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED"));
        } else
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + serializeMethod.apply(value));

        if (!actions.isEmpty()) {
            lore.add("");
            actions.forEach((action, description) -> lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + action + ": " + ChatColor.GRAY + description));
        }

        ItemStack icon = template.clone();
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setLore(lore);
        icon.setItemMeta(iconMeta);

        return icon;
    }

    public static ItemStack createBooleanButton(ItemStack template, boolean value, Map<String, String> actions) {
        return createValueButton(template, value, boolValue -> boolValue ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED"), actions);
    }

    public static Inventory createInventoryWithMinSlots(int minSlots, String title) {
        int inventorySize = minSlots + (9 - minSlots % 9);
        if (minSlots % 9 == 0)
            inventorySize -= 9;
        return Bukkit.createInventory(null, inventorySize, title);
    }

    public static void fillEmptySlotsWithPanes(Inventory inventory, DyeColor paneColor) {
        ItemStack pane = ItemStackHelper.createItem(" ", ItemStackHelper.getVariant(Material.BLACK_STAINED_GLASS_PANE, paneColor), 1);
        IntStream.range(0, inventory.getSize()).filter(i -> inventory.getItem(i) == null).forEach(i -> inventory.setItem(i, pane.clone()));
    }

    public static void openMenuFromDisplay(SimpleDisplay myDisplay, Player player, Elevator elevator, BiConsumer<Player, Elevator> newMethod) {
        myDisplay.stopReturn();
        newMethod.accept(player, elevator);
    }

    public static List<ElevatorAction> getActionsWithSettings(Elevator elevator, boolean up) {
        List<ElevatorAction> actions = new ArrayList<>(up ? elevator.getElevatorType(false).getActionsUp() : elevator.getElevatorType(false).getActionsDown()); // Don't want to alter the original list.
        actions.removeIf(i -> i.getSettings().isEmpty());
        actions.removeIf(i -> i.getSettings().stream().allMatch(s -> s.isSettingGlobalOnly(elevator)));
        return actions;
    }

    public static DialogManager getDialogManager() {
        if(dialogManager == null && VersionHelper.doesVersionSupportDialogs()) {
            registerDialogManager();
        }

        return dialogManager;
    }

    public static void registerDialogManager() {
        if(dialogManager == null && VersionHelper.doesVersionSupportDialogs()) {
            dialogManager = new DialogManager(Elevators.getInstance());
            dialogManager.register();
        }
    }

    public static void unregisterDialogManager() {
        if(dialogManager == null)
            return;
        dialogManager.unregister();
    }
}
