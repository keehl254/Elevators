package me.keehl.elevators.api.models;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class ElevatorRecipe {

    private final IElevatorRecipeGroup recipeGroup;
    private final String permission;
    private final NamespacedKey namespacedKey;
    private final ShapedRecipe recipe;

    public ElevatorRecipe(IElevatorRecipeGroup recipeGroup, String permission, NamespacedKey namespacedKey, ShapedRecipe recipe) {
        this.recipeGroup = recipeGroup;
        this.permission = permission;
        this.namespacedKey = namespacedKey;
        this.recipe = recipe;
    }

    public IElevatorRecipeGroup getRecipeGroup() {
        return this.recipeGroup;
    }

    public String getPermission() {
        return this.permission;
    }

    public NamespacedKey getNamespacedKey() {
        return this.namespacedKey;
    }

    public ShapedRecipe getRecipe() {
        return this.recipe;
    }
}