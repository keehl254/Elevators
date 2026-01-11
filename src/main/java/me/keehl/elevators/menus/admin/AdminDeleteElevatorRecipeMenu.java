package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorRecipeGroup;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.helpers.ElevatorMenuHelper;
import org.bukkit.entity.Player;

public class AdminDeleteElevatorRecipeMenu {

    public static void openAdminDeleteElevatorRecipe(Player player, IElevatorType tempElevatorType, IElevatorRecipeGroup recipeGroup) {
        final IElevatorType elevatorType = Elevators.getElevatorTypeService().getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        ElevatorMenuHelper.openConfirmMenu(player, confirmed -> {
            if (confirmed) {
                elevatorType.getRecipeMap().remove(recipeGroup.getRecipeKey());
                Elevators.getRecipeService().refreshRecipes();
            }

            AdminEditRecipesMenu.openAdminEditRecipesMenu(player, elevatorType);
        });
    }

}
