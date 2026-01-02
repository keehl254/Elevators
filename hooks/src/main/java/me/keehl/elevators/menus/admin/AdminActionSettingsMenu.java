package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.ElevatorAction;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorTypeService;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class AdminActionSettingsMenu {

    public static void openAdminActionSettingsMenu(Player player, ElevatorType tempElevatorType, ElevatorAction action, Runnable onReturn) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        List<ElevatorActionSetting<?>> settings = new ArrayList<>(action.getSettings());

        int inventorySize = (Math.floorDiv(settings.size() + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Settings > Actions > Action");

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory);
        action.onStartEditing(player, display, null);
        display.onReturn(() -> {
            action.onStopEditing(player, display, null);
            onReturn.run();
        });

        for (int i = 0; i < settings.size(); i++) {
            ElevatorActionSetting<?> setting = settings.get(i);
            display.setItemSimple(i + 9, setting.createIcon(setting.getGlobalValue(elevatorType), true), (event, myDisplay) -> {
                myDisplay.stopReturn();
                setting.clickGlobal(player, elevatorType, () -> AdminActionSettingsMenu.openAdminActionSettingsMenu(player, elevatorType, action, onReturn), event);
            });
        }
        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));
        display.open();
    }
}
