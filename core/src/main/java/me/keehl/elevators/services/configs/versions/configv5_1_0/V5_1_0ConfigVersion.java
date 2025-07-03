package me.keehl.elevators.services.configs.versions.configv5_1_0;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.hooks.ProtectionHook;
import me.keehl.elevators.services.ElevatorHookService;
import me.keehl.elevators.services.configs.ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv4_0_2.V4_0_2ConfigRecipe;
import me.keehl.elevators.services.configs.versions.configv4_0_2.V4_0_2ConfigRoot;
import me.keehl.elevators.services.configs.versions.configv5.V5ConfigElevatorType;
import me.keehl.elevators.services.configs.versions.configv5.V5ConfigRecipe;
import me.keehl.elevators.services.configs.versions.configv5.V5ConfigRoot;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import me.keehl.elevators.util.config.RecipeRow;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public class V5_1_0ConfigVersion extends ConfigVersion<V5ConfigRoot, ConfigRoot> {

    @Override
    public ConfigRoot upgradeVersion(V5ConfigRoot currentConfig) {
        Elevators.getElevatorsLogger().info("Converting config from V5.0.0 - V5.1.0");

        // This is a lot of work for simply changing something in ConfigRecipe...
        ConfigRoot newConfig = new ConfigRoot();
        newConfig.updateCheckerEnabled = currentConfig.updateCheckerEnabled;
        newConfig.effectDestination = currentConfig.effectDestination;
        newConfig.permissionMode = currentConfig.permissionMode;
        newConfig.forceFacingUpwards = currentConfig.forceFacingUpwards;
        newConfig.protectionHooks = currentConfig.protectionHooks;
        newConfig.locale = currentConfig.locale;
        newConfig.allowElevatorDispense = currentConfig.allowElevatorDispense;
        newConfig.disabledWorlds = currentConfig.disabledWorlds;
        newConfig.effects = currentConfig.effects;
        newConfig.elevators = new HashMap<>();

        for(String key : currentConfig.elevators.keySet()) {
            V5ConfigElevatorType oldElevatorType = currentConfig.elevators.get(key);
            ElevatorType newElevatorType = new ElevatorType();

            newElevatorType.displayName = oldElevatorType.getDisplayName();
            newElevatorType.usePermission = oldElevatorType.getUsePermission();
            newElevatorType.dyePermission = oldElevatorType.getDyePermission();
            newElevatorType.maxDistance = oldElevatorType.getMaxDistanceAllowedBetweenElevators();
            newElevatorType.maxSolidBlocks = oldElevatorType.getMaxSolidBlocksAllowedBetweenElevators();
            newElevatorType.maxStackSize = oldElevatorType.getMaxStackSize();
            newElevatorType.classCheck = oldElevatorType.checkDestinationElevatorType();
            newElevatorType.stopObstruction = oldElevatorType.shouldStopObstructedTeleport();
            newElevatorType.supportDying = oldElevatorType.canElevatorBeDyed();
            newElevatorType.checkColor = oldElevatorType.shouldValidateSameColor();
            newElevatorType.checkPerms = oldElevatorType.doesElevatorRequirePermissions();
            newElevatorType.canExplode = oldElevatorType.canElevatorExplode();
            newElevatorType.hologramLines = oldElevatorType.getHolographicLines();
            newElevatorType.loreLines = oldElevatorType.getLore();
            newElevatorType.actions = oldElevatorType.getActionsConfig();
            newElevatorType.disabledSettings = oldElevatorType.getDisabledSettings();
            newElevatorType.recipes = new HashMap<>();

            for(String recipeKey : oldElevatorType.getRecipeMap().keySet()) {
                V5ConfigRecipe oldRecipe = oldElevatorType.getRecipeMap().get(recipeKey);
                ElevatorRecipeGroup newRecipe = new ElevatorRecipeGroup();

                newRecipe.defaultOutputColor = oldRecipe.getDefaultOutputColor();
                newRecipe.supportMultiColorOutput = oldRecipe.supportsMultiColorOutput();
                newRecipe.supportMultiColorMaterials = oldRecipe.supportsMultiColorMaterials();
                newRecipe.craftPermission = oldRecipe.getCraftPermission();
                newRecipe.amount = oldRecipe.getAmount();

                List<RecipeRow<NamespacedKey>> keyList = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    RecipeRow<NamespacedKey> keyRow = new RecipeRow<>();
                    if (oldRecipe.getRecipe().size() <= i) {
                        keyList.add(keyRow);
                        continue;
                    }

                    String line = oldRecipe.getRecipe().get(i);

                    keyRow.add(oldRecipe.getMaterials().getOrDefault((!line.isEmpty() ? line.charAt(0) : ' '), Material.AIR).getKey());
                    keyRow.add(oldRecipe.getMaterials().getOrDefault((line.length() > 1 ? line.charAt(1) : ' '), Material.AIR).getKey());
                    keyRow.add(oldRecipe.getMaterials().getOrDefault((line.length() > 2 ? line.charAt(2) : ' '), Material.AIR).getKey());
                    keyList.add(keyRow);
                }
                newRecipe.recipe = keyList;

                newElevatorType.recipes.put(recipeKey, newRecipe);
            }

            newConfig.elevators.put(key, newElevatorType);
            newElevatorType.onLoad();
        }

        return newConfig;
    }
}
