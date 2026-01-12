package me.keehl.elevators.api.services;

import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.permissions.Permissible;

public interface IElevatorRecipeService extends IElevatorService {

    void refreshRecipes();

    void discoverRecipesForPlayer(Player player);

    <T extends Recipe & Keyed> boolean doesPermissibleHavePermissionForRecipe(Permissible permissible, T recipe);
}
