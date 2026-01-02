package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.ElevatorTypeService;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.entity.Player;

public class AdminCreateElevatorTypeMenu {

    public static void openCreateElevatorTypeMenu(Player player) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(result -> {
            if (result == null) {
                AdminMenu.openAdminMenu(player);
                return SimpleInput.SimpleInputResult.STOP;
            }

            if (ElevatorTypeService.getElevatorType(result) != null) {
                MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.nonUniqueElevatorKey);
                return SimpleInput.SimpleInputResult.CONTINUE;
            }

            AdminSettingsMenu.openAdminSettingsMenu(player, ElevatorTypeService.createElevatorType(result));
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(() -> AdminMenu.openAdminMenu(player));
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterMessage);
        input.start();
    }

}
