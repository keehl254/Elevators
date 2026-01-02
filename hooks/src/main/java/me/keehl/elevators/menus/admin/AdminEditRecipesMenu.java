package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ColorHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorTypeService;
import me.keehl.elevators.services.interaction.PagedDisplay;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class AdminEditRecipesMenu {

    public static void openAdminEditRecipesMenu(Player player, ElevatorType tempElevatorType) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        PagedDisplay<ElevatorRecipeGroup> display = new PagedDisplay<>(Elevators.getInstance(), player, elevatorType.getRecipeGroups(), "Admin > Settings > Recipes", () -> AdminSettingsMenu.openAdminSettingsMenu(player, elevatorType));
        display.onCreateItem(recipeGroup -> {

            DyeColor color = DyeColor.getByWoolData((byte) (Math.abs(recipeGroup.getRecipeKey().hashCode()) % 16));
            if (!recipeGroup.supportMultiColorOutput() || color == null)
                color = recipeGroup.getDefaultOutputColor();

            String chatColor = ColorHelper.getChatStringFromColor(color.getColor().asRGB());

            ItemStack icon = ItemStackHelper.createItem(chatColor + ChatColor.BOLD + recipeGroup.getRecipeKey(), ItemStackHelper.getVariant(Material.RED_SHULKER_BOX, color), recipeGroup.amount);
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "Edit Recipe");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Shift Click: " + ChatColor.GRAY + "Delete Recipe");
            meta.setLore(lore);
            icon.setItemMeta(meta);

            return icon;
        });
        display.onClick((item, event, myDisplay) -> {
            myDisplay.stopReturn();
            if (event.isShiftClick())
                AdminDeleteElevatorRecipeMenu.openAdminDeleteElevatorRecipe(player, elevatorType, item);
            else
                AdminEditElevatorRecipeMenu.openAdminEditElevatorRecipeMenu(player, elevatorType, item);
        });

        display.onLoad((tempDisplay, page) -> {
            int addRecipeIndex = display.getDisplay().getInventory().getSize() - 1;
            display.getDisplay().setItemSimple(addRecipeIndex, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Add Recipe", Material.NETHER_STAR, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                AdminEditElevatorRecipeMenu.openAdminEditElevatorRecipeMenu(player, elevatorType, null);
            });
        });

        display.open();
    }
}
