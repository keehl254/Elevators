package com.lkeehl.elevators.services;

import com.lkeehl.elevators.models.ElevatorRecipeGroup;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.configs.ConfigRoot;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.permissions.Permissible;

import java.util.*;

public class ElevatorRecipeService {

    private static boolean initialized = false;

    private static final Map<ElevatorType, ElevatorRecipeGroup> elevatorRecipeGroupMap = new HashMap<>();

    public static void init() {
        if(ElevatorRecipeService.initialized)
            return;

        ConfigService.addConfigCallback(ElevatorRecipeService::unregisterRecipes);

        ElevatorRecipeService.initialized = true;
    }

    private static void unregisterRecipes(ConfigRoot root) {
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();

        List<NamespacedKey> recipesToUnlearn = new ArrayList<>();

        while (it.hasNext()) {
            Recipe recipe = it.next();
            if(!(recipe instanceof ShapedRecipe shapedRecipe))
                continue;
            if(shapedRecipe.getKey().getNamespace().equalsIgnoreCase("elevators")) {
                it.remove();
                recipesToUnlearn.add(shapedRecipe.getKey());
            }
        }

        ElevatorRecipeService.elevatorRecipeGroupMap.clear();

        Bukkit.getOnlinePlayers().forEach(i -> i.undiscoverRecipes(recipesToUnlearn));

    }

    public static void registerElevatorRecipeGroup(ElevatorType elevatorType, ElevatorRecipeGroup recipeGroup) {
        ElevatorRecipeService.elevatorRecipeGroupMap.put(elevatorType, recipeGroup);
        Bukkit.getOnlinePlayers().forEach(i -> i.discoverRecipes(recipeGroup.getNameSpacedKeys()));
    }

    public static void discoverRecipesForPlayer(Player player) {
        for(ElevatorRecipeGroup recipeGroup : elevatorRecipeGroupMap.values())
            player.discoverRecipes(recipeGroup.getNameSpacedKeys());
    }

    public static boolean doesPermissibleHaveCraftPermission(Permissible permissible, ShapedRecipe recipe) {
        return elevatorRecipeGroupMap.values().stream().anyMatch(i -> i.doesPermissibleHavePermissionForRecipe(permissible, recipe));
    }



}
