package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.entity.Player;

public class AdminSaveElevatorRecipeMenu {

    public static void openTextInput(Player player, IElevatorType elevatorType, ElevatorRecipeGroup recipeGroup, Runnable onReturn) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.allowReset();
        input.onComplete(result -> {

            if (elevatorType.getRecipeMap().containsKey(result.toUpperCase())) {
                Elevators.getLocale().getNonUniqueRecipeNameMessage().send(player);
                return SimpleInput.SimpleInputResult.CONTINUE;
            }

            recipeGroup.setKey(result.toUpperCase());
            recipeGroup.load(elevatorType);
            elevatorType.getRecipeMap().put(result.toUpperCase(), recipeGroup);
            onReturn.run();
            Elevators.getRecipeService().refreshRecipes();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(onReturn);
        Elevators.getLocale().getEnterRecipeNameMessage().send(player);
        input.start();
    }

    public static void openSaveElevatorRecipeMenu(Player player, IElevatorType tempElevatorType, ElevatorRecipeGroup recipeGroup) {
        final IElevatorType elevatorType = Elevators.getElevatorTypeService().getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }
        Runnable onReturn = () -> AdminEditRecipesMenu.openAdminEditRecipesMenu(player, elevatorType);
        if (recipeGroup.getRecipeKey() != null) {
            recipeGroup.load(elevatorType);
            elevatorType.getRecipeMap().put(recipeGroup.getRecipeKey(), recipeGroup);
            onReturn.run();
            Elevators.getRecipeService().refreshRecipes();
            return;
        }

        openTextInput(player, elevatorType, recipeGroup, onReturn);
    }
}
