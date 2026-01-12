package me.keehl.elevators.api.models;

import me.keehl.elevators.api.services.configs.versions.IConfigRecipe;
import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.Map;

public interface IElevatorRecipeGroup extends IConfigRecipe {

    void createElevatorRecipes(Map<NamespacedKey, ElevatorRecipe> recipes);

    String getRecipeKey();

    List<NamespacedKey> getNameSpacedKeys();

}
