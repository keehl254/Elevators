package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.VersionHelper;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigRoot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.permissions.Permissible;

import java.util.*;
import java.util.stream.Collectors;

public class ElevatorRecipeService {

    private static boolean initialized = false;

    private static final Map<ElevatorType, ElevatorRecipeGroup> elevatorRecipeGroupMap = new HashMap<>();

    public static void init() {
        if(ElevatorRecipeService.initialized)
            return;
        Elevators.pushAndHoldLog();

        ElevatorConfigService.addConfigCallback(root -> refreshRecipes());

        ElevatorRecipeService.initialized = true;
        Elevators.popLog(logData -> Elevators.log("Recipe service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public static void refreshRecipes() {

        Elevators.pushAndHoldLog();

        ConfigRoot root = ElevatorConfigService.getRootConfig();

        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();

        List<ShapedRecipe> recipesToUnlearn = new ArrayList<>();

        boolean removedRecipes = false;
        while (it.hasNext()) {
            Recipe recipe = it.next();
            if(!(recipe instanceof ShapedRecipe))
                continue;
            ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
            if(shapedRecipe.getKey().getNamespace().equalsIgnoreCase("elevators")) {
                it.remove();
                recipesToUnlearn.add(shapedRecipe);
                removedRecipes = true;
            }
        }

        ElevatorRecipeService.elevatorRecipeGroupMap.clear();

        if(VersionHelper.doesVersionSupportRemoveRecipe())
            recipesToUnlearn.forEach(VersionHelper::removeRecipe);

        Bukkit.getOnlinePlayers().forEach(i -> i.undiscoverRecipes(recipesToUnlearn.stream().map(ShapedRecipe::getKey).collect(Collectors.toList())));

        if(removedRecipes)
            Elevators.log("Unregistered old recipes");

        List<NamespacedKey> recipeKeys = new ArrayList<>();

        int recipes = 0;
        for(ElevatorType elevatorType : root.elevators.values()) {
            for(ElevatorRecipeGroup recipeGroup : elevatorType.getRecipeGroups()) {
                recipeGroup.load(elevatorType);
                ElevatorRecipeService.elevatorRecipeGroupMap.put(elevatorType, recipeGroup);
                recipeKeys.addAll(recipeGroup.getNameSpacedKeys());
                recipes++;
            }
        }

        Bukkit.getOnlinePlayers().forEach(i -> i.discoverRecipes(recipeKeys));

        final int recipeCount = recipes;
        Elevators.popLog(logData -> Elevators.log("Registered " + recipeCount + " recipe groups. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public static void discoverRecipesForPlayer(Player player) {
        for(ElevatorRecipeGroup recipeGroup : ElevatorRecipeService.elevatorRecipeGroupMap.values()) {
            player.discoverRecipes(recipeGroup.getNameSpacedKeys());
        }
    }

    public static boolean doesPermissibleHaveCraftPermission(Permissible permissible, ShapedRecipe recipe) {
        return elevatorRecipeGroupMap.values().stream().anyMatch(i -> i.doesPermissibleHavePermissionForRecipe(permissible, recipe));
    }



}
