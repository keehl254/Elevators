package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.IElevatorRecipeGroup;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.IElevatorRecipeService;
import me.keehl.elevators.api.services.configs.versions.IConfigRoot;
import me.keehl.elevators.helpers.VersionHelper;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.permissions.Permissible;

import java.util.*;
import java.util.stream.Collectors;

public class ElevatorRecipeService extends ElevatorService implements IElevatorRecipeService {

    private boolean initialized = false;

    private final Map<IElevatorType, IElevatorRecipeGroup> elevatorRecipeGroupMap = new HashMap<>();

    public ElevatorRecipeService(IElevators elevators) {
        super(elevators);
    }

    public void onInitialize() {
        if(this.initialized)
            return;
        ElevatorsAPI.pushAndHoldLog();

        Elevators.getConfigService().addConfigCallback(root -> refreshRecipes());

        this.initialized = true;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Recipe service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public void onUninitialize() {

    }

    public void refreshRecipes() {

        ElevatorsAPI.pushAndHoldLog();

        IConfigRoot root = Elevators.getConfigService().getRootConfig();

        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();

        List<ShapedRecipe> recipesToUnlearn = new ArrayList<>();

        boolean removedRecipes = false;
        while (it.hasNext()) {
            Recipe recipe = it.next();
            if(!(recipe instanceof ShapedRecipe shapedRecipe))
                continue;
            if(shapedRecipe.getKey().getNamespace().equalsIgnoreCase("elevators")) {
                it.remove();
                recipesToUnlearn.add(shapedRecipe);
                removedRecipes = true;
            }
        }

        this.elevatorRecipeGroupMap.clear();

        if(VersionHelper.doesVersionSupportRemoveRecipe())
            recipesToUnlearn.forEach(VersionHelper::removeRecipe);

        Bukkit.getOnlinePlayers().forEach(i -> i.undiscoverRecipes(recipesToUnlearn.stream().map(ShapedRecipe::getKey).collect(Collectors.toList())));

        if(removedRecipes)
            ElevatorsAPI.log("Unregistered old recipes");

        List<NamespacedKey> recipeKeys = new ArrayList<>();

        int recipes = 0;
        for(IElevatorType elevatorType : root.getElevators().values()) {
            for(IElevatorRecipeGroup apiRecipeGroup : elevatorType.getRecipeGroups()) {

                if(!(apiRecipeGroup instanceof ElevatorRecipeGroup recipeGroup))
                    continue;

                recipeGroup.load(elevatorType);
                this.elevatorRecipeGroupMap.put(elevatorType, recipeGroup);
                recipeKeys.addAll(recipeGroup.getNameSpacedKeys());
                recipes++;
            }
        }

        Bukkit.getOnlinePlayers().forEach(i -> i.discoverRecipes(recipeKeys));

        final int recipeCount = recipes;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Registered " + recipeCount + " recipe groups. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public void discoverRecipesForPlayer(Player player) {
        for(IElevatorRecipeGroup recipeGroup : this.elevatorRecipeGroupMap.values()) {
            player.discoverRecipes(recipeGroup.getNameSpacedKeys());
        }
    }

    public boolean doesPermissibleHaveCraftPermission(Permissible permissible, ShapedRecipe recipe) {
        return this.elevatorRecipeGroupMap.values().stream().anyMatch(i -> i.doesPermissibleHavePermissionForRecipe(permissible, recipe));
    }



}
