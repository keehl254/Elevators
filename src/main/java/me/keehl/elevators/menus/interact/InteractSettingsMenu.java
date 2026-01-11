package me.keehl.elevators.menus.interact;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorSetting;
import me.keehl.elevators.helpers.ElevatorMenuHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class InteractSettingsMenu {

    public static void openInteractSettingsMenu(Player player, IElevator elevator) {
        if (!elevator.isValid()) {
            InteractMenu.openInteractMenu(player, elevator);
            return;
        }

        List<IElevatorSetting<?>> settings = Elevators.getSettingService().getElevatorSettings().stream().filter(i -> !i.isSettingGlobalOnly(elevator)).toList();

        int itemAmount = settings.size();
        List<IElevatorAction> upActions = ElevatorMenuHelper.getActionsWithSettings(elevator, true);
        List<IElevatorAction> downActions = ElevatorMenuHelper.getActionsWithSettings(elevator, false);

        if (!upActions.isEmpty())
            itemAmount++;
        if (!downActions.isEmpty())
            itemAmount++;

        int inventorySize = (Math.floorDiv(itemAmount + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Elevator > Settings");
        ElevatorMenuHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> InteractMenu.openInteractMenu(player, elevator));
        for (int i = 0; i < settings.size(); i++) {
            IElevatorSetting<?> setting = settings.get(i);
            display.setItemSimple(i + 9, setting.createIcon(setting.getIndividualValue(elevator), false), (event, myDisplay) -> {
                myDisplay.stopReturn();
                setting.clickIndividual(player, elevator, () -> openInteractSettingsMenu(player, elevator), event);
            });
        }

        if (!downActions.isEmpty()) {
            display.setItemSimple(inventory.getSize() - 1, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Downwards Actions", Material.SPECTRAL_ARROW, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                InteractActionsMenu.openInteractActionsMenu(player, elevator, downActions);
            });
        }

        if (!upActions.isEmpty()) {
            display.setItemSimple(inventory.getSize() - (downActions.isEmpty() ? 1 : 2), ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Upwards Actions", Material.TIPPED_ARROW, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                InteractActionsMenu.openInteractActionsMenu(player, elevator, upActions);
            });
        }

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));

        display.open();
    }

}
