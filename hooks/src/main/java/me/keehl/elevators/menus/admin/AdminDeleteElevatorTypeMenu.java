package me.keehl.elevators.menus.admin;

import me.keehl.elevators.helpers.ElevatorMenuHelper;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorTypeService;
import org.bukkit.entity.Player;

public class AdminDeleteElevatorTypeMenu {

    public static void openAdminDeleteElevatorTypeMenu(Player player, ElevatorType tempElevatorType) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        ElevatorMenuHelper.openConfirmMenu(player, confirmed -> {
            if (confirmed)
                ElevatorTypeService.unregisterElevatorType(elevatorType);

            AdminMenu.openAdminMenu(player);
        });
    }

}
