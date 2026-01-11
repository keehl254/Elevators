package me.keehl.elevators.services.configs.versions.configv5_2_0;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.BungeeLocaleComponent;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.configs.ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv5_1_0.*;

import java.util.HashMap;
import java.util.logging.Level;

public class V5_2_0ConfigVersion extends ConfigVersion<V5_1_0ConfigRoot, ConfigRoot> {

    @Override
    public ConfigRoot upgradeVersion(V5_1_0ConfigRoot currentConfig) {
        ElevatorsAPI.log(Level.INFO, "Converting config from V5.1.0 - V5.2.0");

        // This is a lot of work for simply changing something in ConfigRecipe...
        ConfigRoot newConfig = new ConfigRoot();
        newConfig.updateCheckerEnabled = currentConfig.updateCheckerEnabled;
        newConfig.effectDestination = currentConfig.effectDestination;
        newConfig.permissionMode = currentConfig.permissionMode;
        newConfig.forceFacingUpwards = currentConfig.forceFacingUpwards;
        newConfig.hologramServiceEnabled = currentConfig.hologramServiceEnabled;
        if(currentConfig.protectionHooks != null) {
            newConfig.protectionHooks = new HashMap<>();
            for (String pluginKey : currentConfig.protectionHooks.keySet()) {
                V5_1_0ConfigHookData currentHookData = currentConfig.protectionHooks.get(pluginKey);
                ConfigHookData newHookData = new ConfigHookData();
                newHookData.allowCustomization = currentHookData.allowCustomization;
                newHookData.blockNonMemberUseDefault = currentHookData.blockNonMemberUseDefault;

                newConfig.protectionHooks.put(pluginKey, newHookData);
            }
        }

        ConfigLocale newLocale = new ConfigLocale();
        newLocale.cantCreateMessage = new BungeeLocaleComponent(currentConfig.locale.cantCreateMessage);
        newLocale.cantDyeMessage = new BungeeLocaleComponent(currentConfig.locale.cantDyeMessage);
        newLocale.cantUseMessage = new BungeeLocaleComponent(currentConfig.locale.cantUseMessage);
        newLocale.cantGiveMessage = new BungeeLocaleComponent(currentConfig.locale.cantGiveMessage);
        newLocale.cantAdministrateMessage = new BungeeLocaleComponent(currentConfig.locale.cantAdministrateMessage);
        newLocale.cantReloadMessage = new BungeeLocaleComponent(currentConfig.locale.cantReloadMessage);
        newLocale.notEnoughRoomGiveMessage = new BungeeLocaleComponent(currentConfig.locale.notEnoughRoomGiveMessage);
        newLocale.givenElevatorMessage = new BungeeLocaleComponent(currentConfig.locale.givenElevatorMessage);
        newLocale.worldDisabledMessage = new BungeeLocaleComponent(currentConfig.locale.worldDisabledMessage);
        newLocale.elevatorChangedKickedOut = new BungeeLocaleComponent(currentConfig.locale.elevatorChangedKickedOut);
        newLocale.chatInputBackOut = new BungeeLocaleComponent(currentConfig.locale.chatInputBackOut);
        newLocale.chatInputBackOutAllowReset = new BungeeLocaleComponent(currentConfig.locale.chatInputBackOutAllowReset);
        newLocale.enterDisplayName = new BungeeLocaleComponent(currentConfig.locale.enterDisplayName);
        newLocale.enterRecipeName = new BungeeLocaleComponent(currentConfig.locale.enterRecipeName);
        newLocale.enterRecipePermission = new BungeeLocaleComponent(currentConfig.locale.enterRecipePermission);
        newLocale.enterFloorName = new BungeeLocaleComponent(currentConfig.locale.enterFloorName);
        newLocale.enterTitle = new BungeeLocaleComponent(currentConfig.locale.enterTitle);
        newLocale.enterSubtitle = new BungeeLocaleComponent(currentConfig.locale.enterSubtitle);
        newLocale.enterMessage = new BungeeLocaleComponent(currentConfig.locale.enterMessage);
        newLocale.enterElevatorKey = new BungeeLocaleComponent(currentConfig.locale.enterElevatorKey);
        newLocale.nonUniqueElevatorKey = new BungeeLocaleComponent(currentConfig.locale.nonUniqueElevatorKey);
        newLocale.nonUniqueRecipeName = new BungeeLocaleComponent(currentConfig.locale.nonUniqueRecipeName);
        newLocale.enterCommand = new BungeeLocaleComponent(currentConfig.locale.enterCommand);

        newConfig.locale = newLocale;
        newConfig.allowElevatorDispense = currentConfig.allowElevatorDispense;
        newConfig.disabledWorlds = currentConfig.disabledWorlds;

        newConfig.effects = new HashMap<>();
        for(String effectKey : currentConfig.effects.keySet()) {
            ConfigEffect newEffect = new ConfigEffect();
            V5_1_0ConfigEffect currentEffect = currentConfig.effects.get(effectKey);
            newEffect.file = currentEffect.file;
            newEffect.scale = currentEffect.scale;
            newEffect.duration = currentEffect.duration;
            newEffect.useHolo = currentEffect.useHolo;
            newEffect.background = currentEffect.background;

            newConfig.effects.put(effectKey, newEffect);
        }

        newConfig.elevators = new HashMap<>();

        for(String key : currentConfig.elevators.keySet()) {
            V5_1_0ConfigElevatorType oldElevatorType = currentConfig.elevators.get(key);
            ElevatorType newElevatorType = new ElevatorType();

            ConfigSettings configSettings = new ConfigSettings();
            configSettings.usePermission = oldElevatorType.getUsePermission();
            configSettings.dyePermission = oldElevatorType.getDyePermission();
            configSettings.displayName = MessageHelper.getLocaleComponent(oldElevatorType.getDisplayName());
            configSettings.maxDistance = oldElevatorType.getMaxDistanceAllowedBetweenElevators();
            configSettings.maxSolidBlocks = oldElevatorType.getMaxSolidBlocksAllowedBetweenElevators();
            configSettings.maxStackSize = oldElevatorType.getMaxStackSize();
            configSettings.classCheck = oldElevatorType.checkDestinationElevatorType();
            configSettings.stopObstruction = oldElevatorType.shouldStopObstructedTeleport();
            configSettings.supportDying = oldElevatorType.canElevatorBeDyed();
            configSettings.checkColor = oldElevatorType.shouldValidateSameColor();
            configSettings.checkPerms = oldElevatorType.doesElevatorRequirePermissions();
            configSettings.canExplode = oldElevatorType.canElevatorExplode();
            configSettings.hologramLines = MessageHelper.getLocalComponents(oldElevatorType.getHolographicLines());
            configSettings.loreLines = MessageHelper.getLocalComponents(oldElevatorType.getLore());

            newElevatorType.settings = configSettings;

            newElevatorType.actions = new ConfigElevatorType.ConfigActions();
            newElevatorType.actions.up = oldElevatorType.getActionsConfig().up;
            newElevatorType.actions.down = oldElevatorType.getActionsConfig().down;

            newElevatorType.disabledSettings = oldElevatorType.getDisabledSettings();
            newElevatorType.recipes = new HashMap<>();

            for(String recipeKey : oldElevatorType.getRecipeMap().keySet()) {
                V5_1_0ConfigRecipe oldRecipe = oldElevatorType.getRecipeMap().get(recipeKey);
                ElevatorRecipeGroup newRecipe = new ElevatorRecipeGroup();

                newRecipe.defaultOutputColor = oldRecipe.getDefaultOutputColor();
                newRecipe.supportMultiColorOutput = oldRecipe.supportsMultiColorOutput();
                newRecipe.supportMultiColorMaterials = oldRecipe.supportsMultiColorMaterials();
                newRecipe.craftPermission = oldRecipe.getCraftPermission();
                newRecipe.amount = oldRecipe.getAmount();
                newRecipe.recipe = oldRecipe.getRecipe();

                newElevatorType.recipes.put(recipeKey, newRecipe);
            }

            newConfig.elevators.put(key, newElevatorType);
            newElevatorType.onLoad();
        }

        return newConfig;
    }
}
