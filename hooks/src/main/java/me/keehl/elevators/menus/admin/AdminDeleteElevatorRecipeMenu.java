package me.keehl.elevators.menus.admin;

import me.keehl.elevators.helpers.ElevatorMenuHelper;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorRecipeService;
import me.keehl.elevators.services.ElevatorTypeService;
import org.bukkit.entity.Player;

public class AdminDeleteElevatorRecipeMenu {

    public static void openAdminDeleteElevatorRecipe(Player player, ElevatorType tempElevatorType, ElevatorRecipeGroup recipeGroup) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        ElevatorMenuHelper.openConfirmMenu(player, confirmed -> {
            if (confirmed) {
                elevatorType.getRecipeMap().remove(recipeGroup.getRecipeKey());
                ElevatorRecipeService.refreshRecipes();
            }

            AdminEditRecipesMenu.openAdminEditRecipesMenu(player, elevatorType);
        });
    }

}
