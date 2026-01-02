package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ItemStackHelper;
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
public class AdminMenu {

    public static void openAdminMenu(Player player) {

        PagedDisplay<ElevatorType> display = new PagedDisplay<>(Elevators.getInstance(), player, ElevatorTypeService.getExistingElevatorTypes(), "Admin");
        display.onCreateItem(elevatorType -> {

            DyeColor color = DyeColor.getByWoolData((byte) (Math.abs(elevatorType.getTypeKey().hashCode()) % 16));
            if (elevatorType.canElevatorBeDyed() && !elevatorType.getRecipeGroups().isEmpty())
                color = elevatorType.getRecipeGroups().getFirst().getDefaultOutputColor();

            ItemStack icon = ItemStackHelper.createItemStackFromElevatorType(elevatorType, color);
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Type Key: " + elevatorType.getTypeKey());
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "Edit Elevator");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Shift Click: " + ChatColor.GRAY + "Delete Elevator");
            meta.setLore(lore);
            icon.setItemMeta(meta);

            return icon;
        });
        display.onClick((item, event, myDisplay) -> {
            myDisplay.stopReturn();
            if (event.isShiftClick())
                AdminDeleteElevatorTypeMenu.openAdminDeleteElevatorTypeMenu(player, item);
            else
                AdminSettingsMenu.openAdminSettingsMenu(player, item);
        });

        display.onLoad((tempDisplay, page) -> {
            int addElevatorIndex = display.getDisplay().getInventory().getSize() - 1;
            display.getDisplay().setItemSimple(addElevatorIndex, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Add Elevator", Material.NETHER_STAR, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                AdminCreateElevatorTypeMenu.openCreateElevatorTypeMenu(player);
            });
        });

        display.open();

    }

}
