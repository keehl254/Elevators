package me.keehl.elevators.services.configs.versions.configv2;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.services.configs.ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv1.V1ConfigRoot;

public class V2ConfigVersion extends ConfigVersion<V1ConfigRoot, V2ConfigRoot> {

    @Override
    public V2ConfigRoot upgradeVersion(V1ConfigRoot currentConfig) {
        Elevators.getElevatorsLogger().info("Converting config from V2.0.0 - V3.0.0");

        V2ConfigRoot newConfig = new V2ConfigRoot();
        newConfig.cantCreateMessage = currentConfig.cantCreateMessage;
        newConfig.cantUseMessage = currentConfig.cantUseMessage;
        newConfig.cantGiveMessage = currentConfig.cantGiveMessage;
        newConfig.cantReloadMessage = currentConfig.cantReloadMessage;
        newConfig.notEnoughRoomGiveMessage = currentConfig.notEnoughRoomGive;
        newConfig.givenElevatorMessage = currentConfig.givenElevatorMessage;

        newConfig.worldSounds = currentConfig.worldSounds;
        newConfig.stopObstruction = currentConfig.stopObstruction;
        newConfig.maxStackSize = currentConfig.maxStackSize;
        newConfig.volume = currentConfig.volume;
        newConfig.pitch = currentConfig.pitch;

        newConfig.maxDistance = currentConfig.maxBlockDistance;
        newConfig.maxSolidBlocks = currentConfig.maxSolidBlocks == 0 ? -1 : currentConfig.maxSolidBlocks; // V1 config had 0 as infinite. V2 has -1 as infinite.
        newConfig.coloredOutput = currentConfig.colouredWoolCrafting;
        newConfig.checkColor = currentConfig.colorCheck;

        // Not carrying over the usePerms config value from V1 because V1's perm system was trash.

        newConfig.canExplode = currentConfig.canExplode;
        newConfig.playSound = currentConfig.playSound;
        newConfig.sound = currentConfig.sound;

        V2ConfigRecipe recipe = new V2ConfigRecipe();
        recipe.amount = currentConfig.recipeAmount;
        recipe.materials = currentConfig.materials;
        recipe.recipe = currentConfig.recipe;
        recipe.coloredCrafting = currentConfig.colouredWoolCrafting;
        recipe.permission = "elevators.create";

        newConfig.recipes.put("classic",  recipe);

        return newConfig;
    }

}
