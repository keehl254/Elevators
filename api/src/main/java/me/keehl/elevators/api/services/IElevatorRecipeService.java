package me.keehl.elevators.api.services;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.permissions.Permissible;

public interface IElevatorRecipeService extends IElevatorService {

    void refreshRecipes();

    void discoverRecipesForPlayer(Player player);

    boolean doesPermissibleHaveCraftPermission(Permissible permissible, ShapedRecipe recipe);
}
