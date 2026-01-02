package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.ElevatorRecipeService;
import me.keehl.elevators.services.ElevatorTypeService;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.entity.Player;

public class AdminSaveElevatorRecipeMenu {

    public static void openTextInput(Player player, ElevatorType elevatorType, ElevatorRecipeGroup recipeGroup, Runnable onReturn) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.allowReset();
        input.onComplete(result -> {

            if (elevatorType.getRecipeMap().containsKey(result.toUpperCase())) {
                MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.nonUniqueRecipeName);
                return SimpleInput.SimpleInputResult.CONTINUE;
            }

            recipeGroup.setKey(result.toUpperCase());
            elevatorType.getRecipeMap().put(result.toUpperCase(), recipeGroup);
            onReturn.run();
            ElevatorRecipeService.refreshRecipes();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(onReturn);
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterRecipeName);
        input.start();
    }

    public static void openSaveElevatorRecipeMenu(Player player, ElevatorType tempElevatorType, ElevatorRecipeGroup recipeGroup) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }
        Runnable onReturn = () -> AdminEditRecipesMenu.openAdminEditRecipesMenu(player, elevatorType);
        if (recipeGroup.getRecipeKey() != null) {
            elevatorType.getRecipeMap().put(recipeGroup.getRecipeKey(), recipeGroup);
            onReturn.run();
            ElevatorRecipeService.refreshRecipes();
            return;
        }

        openTextInput(player, elevatorType, recipeGroup, onReturn);
    }
}
