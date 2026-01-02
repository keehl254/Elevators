package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.ElevatorAction;
import me.keehl.elevators.models.ElevatorSetting;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorSettingService;
import me.keehl.elevators.services.ElevatorTypeService;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

@SuppressWarnings("deprecation")
public class AdminSettingsMenu {

    public static void openAdminSettingsMenu(Player player, ElevatorType tempElevatorType) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        List<ElevatorSetting<?>> settings = ElevatorSettingService.getElevatorSettings();

        int itemAmount = settings.size() + 3;
        List<ElevatorAction> upActions = elevatorType.getActionsUp();
        List<ElevatorAction> downActions = elevatorType.getActionsDown();

        int inventorySize = (Math.floorDiv(itemAmount + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Admin > Settings");

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> AdminMenu.openAdminMenu(player));
        for (int i = 0; i < settings.size(); i++) {
            ElevatorSetting<?> setting = settings.get(i);
            display.setItemSimple(i + 9, setting.createIcon(setting.getGlobalValue(elevatorType), true), (event, myDisplay) -> {
                myDisplay.stopReturn();
                setting.clickGlobal(player, elevatorType, () -> openAdminSettingsMenu(player, elevatorType), event);
            });
        }

        display.setItemSimple(inventory.getSize() - 3, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Recipes", Material.MAP, 1), (event, myDisplay) -> {
            myDisplay.stopReturn();
            AdminEditRecipesMenu.openAdminEditRecipesMenu(player, elevatorType);
        });

        display.setItemSimple(inventory.getSize() - 1, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Downwards Actions", Material.SPECTRAL_ARROW, 1), (event, myDisplay) -> {
            myDisplay.stopReturn();
            AdminActionsMenu.openAdminActionsMenu(player, elevatorType, downActions);
        });

        display.setItemSimple(inventory.getSize() - 2, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Upwards Actions", Material.TIPPED_ARROW, 1), (event, myDisplay) -> {
            myDisplay.stopReturn();
            AdminActionsMenu.openAdminActionsMenu(player, elevatorType, upActions);
        });

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));

        display.open();
    }
}
