package me.keehl.elevators.api.models;

import me.keehl.elevators.api.services.configs.versions.IConfigRecipe;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.permissions.Permissible;

import java.util.List;

public interface IElevatorRecipeGroup extends IConfigRecipe {

    void refreshRecipes();

    String getRecipeKey();

    /* Note to anyone working with this method: A new recipe is registered for each color, so the namespacedKey check
    on the last line is enough for checking colored crafting permission.
     */
    <T extends Recipe & Keyed> boolean doesPermissibleHavePermissionForRecipe(Permissible permissible, T recipe);

    List<NamespacedKey> getNameSpacedKeys();

}
