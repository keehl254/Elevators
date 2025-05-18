package com.lkeehl.elevators.services.configs.versions.configv4_0_2;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.services.configs.ConfigVersion;
import com.lkeehl.elevators.services.configs.versions.configv4.V4ConfigRecipe;
import com.lkeehl.elevators.services.configs.versions.configv4.V4ConfigRoot;

public class V4_0_2ConfigVersion extends ConfigVersion<V4ConfigRoot, V4_0_2ConfigRoot> {
    @Override
    public V4_0_2ConfigRoot upgradeVersion(V4ConfigRoot currentConfig) {
        Elevators.getElevatorsLogger().info("Converting config from V4.0.0 - V4.0.2");

        V4_0_2ConfigRoot newConfig = new V4_0_2ConfigRoot();
        newConfig.updateCheckerEnabled = currentConfig.updateCheckerEnabled;

        newConfig.cantCreateMessage = currentConfig.cantCreateMessage;
        newConfig.cantUseMessage = currentConfig.cantUseMessage;
        newConfig.cantGiveMessage = currentConfig.cantGiveMessage;
        newConfig.cantReloadMessage = currentConfig.cantReloadMessage;
        newConfig.notEnoughRoomGiveMessage = currentConfig.notEnoughRoomGiveMessage;
        newConfig.givenElevatorMessage = currentConfig.givenElevatorMessage;
        newConfig.worldDisabledMessage = currentConfig.worldDisabledMessage;
        newConfig.elevatorNowProtected = currentConfig.elevatorNowProtected;
        newConfig.elevatorNowUnprotected = currentConfig.elevatorNowUnprotected;

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

        // Time to start on our actions.
        V4_0_2ConfigActions actions = new V4_0_2ConfigActions();

        // Add our sound actions.
        if(currentConfig.playSound) {
            actions.up.add(String.format("sound: %s volume=%f pitch=%f", currentConfig.sound, currentConfig.volume, currentConfig.pitch));
            actions.down.add(String.format("sound: %s volume=%f pitch=%f", currentConfig.sound, currentConfig.volume, currentConfig.pitch));
        }

        // Add our command actions
        for(String command : currentConfig.commands.up) {
            boolean playerCommand = command.toLowerCase().startsWith("player-");
            String action = playerCommand ? "command-player" : "command-server";
            String data = command.replace(playerCommand ? "player-" : "console-", "").trim() ;
            actions.up.add(String.format("%s: %s", action, data));
        }

        for(String command : currentConfig.commands.down) {
            boolean playerCommand = command.toLowerCase().startsWith("player-");
            String action = playerCommand ? "command-player" : "command-server";
            String data = command.replace(playerCommand ? "player-" : "console-", "").trim() ;
            actions.down.add(String.format("%s: %s", action, data));
        }
        newConfig.actions = actions;

        for(String key : currentConfig.recipes.keySet()) {

            V4ConfigRecipe oldRecipe = currentConfig.recipes.get(key);
            V4_0_2ConfigRecipe newRecipe = new V4_0_2ConfigRecipe();

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
