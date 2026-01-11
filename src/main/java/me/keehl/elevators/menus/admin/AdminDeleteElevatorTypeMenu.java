package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.helpers.ElevatorMenuHelper;
import org.bukkit.entity.Player;

public class AdminDeleteElevatorTypeMenu {

    public static void openAdminDeleteElevatorTypeMenu(Player player, IElevatorType tempElevatorType) {
        final IElevatorType elevatorType = Elevators.getElevatorTypeService().getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        ElevatorMenuHelper.openConfirmMenu(player, confirmed -> {
            if (confirmed)
                Elevators.getElevatorTypeService().unregisterElevatorType(elevatorType);

            AdminMenu.openAdminMenu(player);
        });
    }

}
