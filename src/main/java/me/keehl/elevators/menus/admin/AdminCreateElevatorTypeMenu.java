package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
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

            if (Elevators.getElevatorTypeService().getElevatorType(result) != null) {
                Elevators.getLocale().getNonUniqueElevatorKeyMessage().send(player);
                return SimpleInput.SimpleInputResult.CONTINUE;
            }

            AdminSettingsMenu.openAdminSettingsMenu(player, Elevators.getElevatorTypeService().createElevatorType(result));
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(() -> AdminMenu.openAdminMenu(player));
        Elevators.getLocale().getEnterMessageMessage().send(player);
        input.start();
    }

}
