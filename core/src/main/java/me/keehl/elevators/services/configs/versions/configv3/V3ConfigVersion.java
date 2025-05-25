package me.keehl.elevators.services.configs.versions.configv3;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.services.configs.ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv2.V2ConfigRecipe;
import me.keehl.elevators.services.configs.versions.configv2.V2ConfigRoot;

public class V3ConfigVersion extends ConfigVersion<V2ConfigRoot, V3ConfigRoot> {


    @Override
    public V3ConfigRoot upgradeVersion(V2ConfigRoot currentConfig) {
        Elevators.getElevatorsLogger().info("Converting config from V3.0.0 - V4.0.0");

        V3ConfigRoot newConfig = new V3ConfigRoot();
        newConfig.cantCreateMessage = currentConfig.cantCreateMessage;
        newConfig.cantUseMessage = currentConfig.cantUseMessage;
        newConfig.cantGiveMessage = currentConfig.cantGiveMessage;
        newConfig.cantReloadMessage = currentConfig.cantReloadMessage;
        newConfig.notEnoughRoomGiveMessage = currentConfig.notEnoughRoomGiveMessage;
        newConfig.givenElevatorMessage = currentConfig.givenElevatorMessage;

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

            V2ConfigRecipe oldRecipe = currentConfig.recipes.get(key);
            V3ConfigRecipe newRecipe = new V3ConfigRecipe();

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
