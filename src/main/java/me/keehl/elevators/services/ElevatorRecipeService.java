package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.ElevatorRecipe;
import me.keehl.elevators.api.models.IElevatorRecipeGroup;
import me.keehl.elevators.api.services.IElevatorRecipeService;
import me.keehl.elevators.api.services.configs.versions.IConfigRoot;
import me.keehl.elevators.helpers.VersionHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.permissions.Permissible;
import java.util.*;

public class ElevatorRecipeService extends ElevatorService implements IElevatorRecipeService {

    private boolean initialized = false;

    private Map<NamespacedKey, ElevatorRecipe> loadedRecipes = new HashMap<>();

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
        List<IElevatorRecipeGroup> recipeGroups = root.getElevators().values().stream().flatMap(x ->x.getRecipeGroups().stream()).toList();

        Map<NamespacedKey, ElevatorRecipe> newRecipes = new HashMap<>();
        for(IElevatorRecipeGroup recipeGroup : recipeGroups) {
            recipeGroup.createElevatorRecipes(newRecipes);
        }

        List<NamespacedKey> toRemove = new ArrayList<>(this.loadedRecipes.keySet().stream().filter(x -> !newRecipes.containsKey(x)).toList());
        List<ElevatorRecipe> toAdd = new ArrayList<>(newRecipes.values().stream().filter(x -> !this.loadedRecipes.containsKey(x.getNamespacedKey())).toList());
        for(ElevatorRecipe oldRecipe : this.loadedRecipes.values()) {
            if(toRemove.contains(oldRecipe.getNamespacedKey()))
                continue;

            ElevatorRecipe newRecipe = newRecipes.get(oldRecipe.getNamespacedKey());
            Map<Character, RecipeChoice> oldChoices = oldRecipe.getRecipe().getChoiceMap();
            Map<Character, RecipeChoice> newChoices = newRecipe.getRecipe().getChoiceMap();

            for(Character character : oldChoices.keySet()) {
                RecipeChoice oldChoice = oldChoices.get(character);
                RecipeChoice newChoice = newChoices.get(character);

                if(!oldChoice.equals(newChoice)) {
                    toRemove.add(oldRecipe.getNamespacedKey());
                    toAdd.add(newRecipe);
                    break;
                }
            }
            if(toRemove.contains(oldRecipe.getNamespacedKey()))
                continue;

            if(!newRecipe.getRecipe().getResult().equals(oldRecipe.getRecipe().getResult())) {
                toRemove.add(oldRecipe.getNamespacedKey());
                toAdd.add(newRecipe);
            }
        }

        this.loadedRecipes = newRecipes;

        toRemove.forEach(VersionHelper::removeRecipe);
        toAdd.forEach(x -> Bukkit.addRecipe(x.getRecipe()));

        //Bukkit.getOnlinePlayers().forEach(i -> i.discoverRecipes(recipeKeys));

        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Registered " + recipeGroups.size() +" recipe groups. (" + newRecipes.size() + " recipes) "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public void discoverRecipesForPlayer(Player player) {
        player.discoverRecipes(this.loadedRecipes.keySet());
    }

    /* Note to anyone working with this method: A new recipe is registered for each color, so the namespacedKey check
    on the last line is enough for checking colored crafting permission.
     */
    public <T extends Recipe & Keyed> boolean doesPermissibleHavePermissionForRecipe(Permissible permissible, T recipe) {

        ElevatorRecipe elevatorRecipe = this.loadedRecipes.getOrDefault(recipe.getKey(), null);
        if(elevatorRecipe == null)
            return false;

        IElevatorRecipeGroup recipeGroup = elevatorRecipe.getRecipeGroup();
        if (!recipeGroup.supportsMultiColorMaterials())
            return permissible.hasPermission(recipeGroup.getCraftPermission());
        if (permissible.hasPermission(recipeGroup.getCraftPermission() + ".*"))
            return true;

        return permissible.hasPermission(elevatorRecipe.getPermission());
    }



}
