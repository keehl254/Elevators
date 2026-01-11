package me.keehl.elevators.menus.interact;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.helpers.ElevatorMenuHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class InteractActionsMenu {

    public static void openInteractActionsMenu(final Player player, final IElevator elevator, final List<IElevatorAction> actions) {
        if (!elevator.isValid()) {
            InteractSettingsMenu.openInteractSettingsMenu(player, elevator);
            return;
        }

        int inventorySize = (Math.floorDiv(actions.size() + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Elevator > Settings > Actions");
        ElevatorMenuHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> InteractSettingsMenu.openInteractSettingsMenu(player, elevator));
        for (int i = 0; i < actions.size(); i++) {
            IElevatorAction action = actions.get(i);
            display.setItemSimple(i + 9, action.getIcon(), (event, myDisplay) -> {
                myDisplay.stopReturn();
                InteractActionSettingsMenu.openInteractActionSettingsMenu(player, elevator, action, () -> InteractActionsMenu.openInteractActionsMenu(player, elevator, actions));
            });
        }
        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));
        display.open();
    }

}
