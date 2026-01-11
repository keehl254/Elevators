package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.services.interaction.PagedDisplay;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminCreateActionMenu {

    public static void openAdminCreateActionMenu(Player player, IElevatorType tempElevatorType, List<IElevatorAction> currentActionList) {
        final IElevatorType elevatorType = Elevators.getElevatorTypeService().getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        List<String> registeredActions = Elevators.getActionService().getRegisteredActions();

        PagedDisplay<String> display = new PagedDisplay<>(Elevators.getInstance(), player, registeredActions, "Settings > Actions > Create", () -> AdminSettingsMenu.openAdminSettingsMenu(player, elevatorType));
        display.onCreateItem(Elevators.getActionService()::getActionIcon);
        display.onClick((item, event, myDisplay) -> {
            myDisplay.stopReturn();
            IElevatorAction newAction = Elevators.getActionService().createBlankAction(elevatorType, item);
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
