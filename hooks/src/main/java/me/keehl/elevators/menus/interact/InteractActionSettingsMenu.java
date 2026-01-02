package me.keehl.elevators.menus.interact;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.helpers.ElevatorMenuHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorAction;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class InteractActionSettingsMenu {

    public static void openInteractActionSettingsMenu(Player player, Elevator elevator, ElevatorAction action, Runnable onReturn) {
        if (!elevator.isValid()) {
            onReturn.run();
            return;
        }

        List<ElevatorActionSetting<?>> settings = new ArrayList<>(action.getSettings());
        settings.removeIf(i -> i.isSettingGlobalOnly(elevator));

        int inventorySize = (Math.floorDiv(settings.size() + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Settings > Actions > Action");
        ElevatorMenuHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory);
        action.onStartEditing(player, display, elevator);
        display.onReturn(() -> {
            action.onStopEditing(player, display, elevator);
            onReturn.run();
        });

        for (int i = 0; i < settings.size(); i++) {
            ElevatorActionSetting<?> setting = settings.get(i);
            display.setItemSimple(i + 9, setting.createIcon(setting.getIndividualValue(elevator), false), (event, myDisplay) -> {
                myDisplay.stopReturn();
                setting.clickIndividual(player, elevator, () -> InteractActionSettingsMenu.openInteractActionSettingsMenu(player, elevator, action, onReturn), event);
            });
        }
        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));
        display.open();
    }

}
