package me.keehl.elevators.services.configs.versions.configv5_1_0;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.services.configs.ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv5.*;
import me.keehl.elevators.util.config.RecipeRow;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class V5_1_0ConfigVersion extends ConfigVersion<V5ConfigRoot, V5_1_0ConfigRoot> {

    @Override
    public V5_1_0ConfigRoot upgradeVersion(V5ConfigRoot currentConfig) {
        Elevators.log(Level.INFO, "Converting config from V5.0.0 - V5.1.0");

        // This is a lot of work for simply changing something in ConfigRecipe...
        V5_1_0ConfigRoot newConfig = new V5_1_0ConfigRoot();
        newConfig.updateCheckerEnabled = currentConfig.updateCheckerEnabled;
        newConfig.effectDestination = currentConfig.effectDestination;
        newConfig.permissionMode = currentConfig.permissionMode;
        newConfig.forceFacingUpwards = currentConfig.forceFacingUpwards;

        if(currentConfig.protectionHooks != null) {
            newConfig.protectionHooks = new HashMap<>();
            for (String pluginKey : currentConfig.protectionHooks.keySet()) {
                V5ConfigHookData currentHookData = currentConfig.protectionHooks.get(pluginKey);
                V5_1_0ConfigHookData newHookData = new V5_1_0ConfigHookData();
                newHookData.allowCustomization = currentHookData.allowCustomization;
                newHookData.blockNonMemberUseDefault = currentHookData.blockNonMemberUseDefault;

                newConfig.protectionHooks.put(pluginKey, newHookData);
            }
        }

        V5_1_0ConfigLocale newLocale = new V5_1_0ConfigLocale();
        newLocale.cantCreateMessage = currentConfig.locale.cantCreateMessage;
        newLocale.cantDyeMessage = currentConfig.locale.cantDyeMessage;
        newLocale.cantUseMessage = currentConfig.locale.cantUseMessage;
        newLocale.cantGiveMessage = currentConfig.locale.cantGiveMessage;
        newLocale.cantAdministrateMessage = currentConfig.locale.cantAdministrateMessage;
        newLocale.cantReloadMessage = currentConfig.locale.cantReloadMessage;
        newLocale.notEnoughRoomGiveMessage = currentConfig.locale.notEnoughRoomGiveMessage;
        newLocale.givenElevatorMessage = currentConfig.locale.givenElevatorMessage;
        newLocale.worldDisabledMessage = currentConfig.locale.worldDisabledMessage;
        newLocale.elevatorChangedKickedOut = currentConfig.locale.elevatorChangedKickedOut;
        newLocale.chatInputBackOut = currentConfig.locale.chatInputBackOut;
        newLocale.chatInputBackOutAllowReset = currentConfig.locale.chatInputBackOutAllowReset;
        newLocale.enterDisplayName = currentConfig.locale.enterDisplayName;
        newLocale.enterRecipeName = currentConfig.locale.enterRecipeName;
        newLocale.enterRecipePermission = currentConfig.locale.enterRecipePermission;
        newLocale.enterFloorName = currentConfig.locale.enterFloorName;
        newLocale.enterTitle = currentConfig.locale.enterTitle;
        newLocale.enterSubtitle = currentConfig.locale.enterSubtitle;
        newLocale.enterMessage = currentConfig.locale.enterMessage;
        newLocale.enterElevatorKey = currentConfig.locale.enterElevatorKey;
        newLocale.nonUniqueElevatorKey = currentConfig.locale.nonUniqueElevatorKey;
        newLocale.nonUniqueRecipeName = currentConfig.locale.nonUniqueRecipeName;
        newLocale.enterCommand = currentConfig.locale.enterCommand;

        newConfig.locale = newLocale;
        newConfig.allowElevatorDispense = currentConfig.allowElevatorDispense;
        newConfig.disabledWorlds = currentConfig.disabledWorlds;

        newConfig.effects = new HashMap<>();
        for(String effectKey : currentConfig.effects.keySet()) {
            V5_1_0ConfigEffect newEffect = new V5_1_0ConfigEffect();
            V5ConfigEffect currentEffect = currentConfig.effects.get(effectKey);
            newEffect.file = currentEffect.file;
            newEffect.scale = currentEffect.scale;
            newEffect.duration = currentEffect.duration;
            newEffect.useHolo = currentEffect.useHolo;
            newEffect.background = currentEffect.background;

            newConfig.effects.put(effectKey, newEffect);
        }

        newConfig.elevators = new HashMap<>();

        for(String key : currentConfig.elevators.keySet()) {
            V5ConfigElevatorType oldElevatorType = currentConfig.elevators.get(key);
            V5_1_0ConfigElevatorType newElevatorType = new V5_1_0ConfigElevatorType();

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

            newElevatorType.actions = new V5_1_0ConfigElevatorType.ConfigActions();
            newElevatorType.actions.up = oldElevatorType.getActionsConfig().up;
            newElevatorType.actions.down = oldElevatorType.getActionsConfig().down;

            newElevatorType.disabledSettings = oldElevatorType.getDisabledSettings();
            newElevatorType.recipes = new HashMap<>();

            for(String recipeKey : oldElevatorType.getRecipeMap().keySet()) {
                V5ConfigRecipe oldRecipe = oldElevatorType.getRecipeMap().get(recipeKey);
                V5_1_0ConfigRecipe newRecipe = new V5_1_0ConfigRecipe();

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
