package me.keehl.elevators.services.configs.versions.configv4;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.services.configs.ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv3.V3ConfigRecipe;
import me.keehl.elevators.services.configs.versions.configv3.V3ConfigRoot;

import java.util.logging.Level;

public class V4ConfigVersion extends ConfigVersion<V3ConfigRoot, V4ConfigRoot> {
    @Override
    public V4ConfigRoot upgradeVersion(V3ConfigRoot currentConfig) {
        ElevatorsAPI.log(Level.INFO, "Converting config from V3.0.0 - V4.0.0");

        V4ConfigRoot newConfig = new V4ConfigRoot();
        newConfig.updateCheckerEnabled = currentConfig.updateCheckerEnabled;

        newConfig.cantCreateMessage = currentConfig.cantCreateMessage;
        newConfig.cantUseMessage = currentConfig.cantUseMessage;
        newConfig.cantGiveMessage = currentConfig.cantGiveMessage;
        newConfig.cantReloadMessage = currentConfig.cantReloadMessage;
        newConfig.notEnoughRoomGiveMessage = currentConfig.notEnoughRoomGiveMessage;
        newConfig.givenElevatorMessage = currentConfig.givenElevatorMessage;
        newConfig.worldDisabledMessage = currentConfig.worldDisabledMessage;

        newConfig.supportClaims = currentConfig.supportClaims;
        newConfig.claimProtectionDefault = currentConfig.claimProtectionDefault;

        newConfig.disabledWorlds = currentConfig.disabledWorlds;

        newConfig.worldSounds = currentConfig.worldSounds;
        newConfig.forceFacingUpwards = currentConfig.forceFacingUpwards;
        newConfig.displayName = currentConfig.displayName;
        newConfig.maxDistance = currentConfig.maxDistance;
        newConfig.maxSolidBlocks = currentConfig.maxSolidBlocks;
        newConfig.maxStackSize = currentConfig.maxStackSize;
        newConfig.coloredOutput = currentConfig.coloredOutput;
        newConfig.checkColor = currentConfig.checkColor;
        newConfig.stopObstruction = currentConfig.stopObstruction;
        newConfig.checkPerms = currentConfig.checkPerms;
        newConfig.canExplode = currentConfig.canExplode;
        newConfig.playSound = currentConfig.playSound;
        newConfig.volume = currentConfig.volume;
        newConfig.pitch = currentConfig.pitch;
        newConfig.sound = currentConfig.sound;
        newConfig.defaultColor = currentConfig.defaultColor;
        newConfig.lore = currentConfig.lore;

        newConfig.commands.up = currentConfig.commands.up;
        newConfig.commands.down = currentConfig.commands.down;

        for(String key : currentConfig.recipes.keySet()) {

            V3ConfigRecipe oldRecipe = currentConfig.recipes.get(key);
            V4ConfigRecipe newRecipe = new V4ConfigRecipe();

            newRecipe.amount = oldRecipe.amount;
            newRecipe.permission = oldRecipe.permission;
            newRecipe.coloredCrafting = oldRecipe.coloredCrafting;
            newRecipe.recipe = oldRecipe.recipe;
            newRecipe.materials = oldRecipe.materials;

            newConfig.recipes.put(key, newRecipe);
        }

        return newConfig;

    }
}
