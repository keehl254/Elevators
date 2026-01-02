package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.ElevatorAction;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorActionService;
import me.keehl.elevators.services.ElevatorTypeService;
import me.keehl.elevators.services.interaction.PagedDisplay;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminCreateActionMenu {

    public static void openAdminCreateActionMenu(Player player, ElevatorType tempElevatorType, List<ElevatorAction> currentActionList) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        List<String> registeredActions = ElevatorActionService.getRegisteredActions();

        PagedDisplay<String> display = new PagedDisplay<>(Elevators.getInstance(), player, registeredActions, "Settings > Actions > Create", () -> AdminSettingsMenu.openAdminSettingsMenu(player, elevatorType));
        display.onCreateItem(ElevatorActionService::getActionIcon);
        display.onClick((item, event, myDisplay) -> {
            myDisplay.stopReturn();
            ElevatorAction newAction = ElevatorActionService.createBlankAction(elevatorType, item);
            if (newAction != null) {
                currentActionList.add(newAction);

                if (!newAction.getSettings().isEmpty()) {
                    AdminActionSettingsMenu.openAdminActionSettingsMenu(player, elevatorType, newAction, () -> AdminActionsMenu.openAdminActionsMenu(player, elevatorType, currentActionList));
                    return;
                }
            }
            AdminActionsMenu.openAdminActionsMenu(player, elevatorType, currentActionList);
        });

        display.open();

    }
}
