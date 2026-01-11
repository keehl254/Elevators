package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigRecipe;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.entity.Player;

public class AdminEditRecipePermissionMenu {

    public static void openEditRecipePermissionMenu(Player player, IElevatorType tempElevatorType, ElevatorRecipeGroup recipeGroup) {
        final IElevatorType elevatorType = Elevators.getElevatorTypeService().getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.allowReset();

        input.onComplete(result -> {

            if (result == null)
                result = "elevators.craft." + elevatorType.getTypeKey();

            ConfigRecipe.setCraftPermission(recipeGroup, result);
            AdminEditElevatorRecipeMenu.openAdminEditElevatorRecipeMenu(player, elevatorType, recipeGroup);
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(() -> AdminEditElevatorRecipeMenu.openAdminEditElevatorRecipeMenu(player, elevatorType, recipeGroup));
        Elevators.getLocale().getEnterRecipePermissionMessage().send(player);
        input.start();

    }
}
