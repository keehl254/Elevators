package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.effects.ArrowEffect;
import me.keehl.elevators.effects.HelixEffect;
import me.keehl.elevators.effects.ImageEffect;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.models.ElevatorEffect;
import me.keehl.elevators.services.configs.versions.configv5.ConfigEffect;
import me.keehl.elevators.services.configs.versions.configv5.ConfigRoot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElevatorEffectService {

    private static boolean initialized = false;

    private static final Map<String, ElevatorEffect> elevatorEffects = new HashMap<>();

    public static void init() {
        if(ElevatorEffectService.initialized)
            return;

        ElevatorConfigService.addConfigCallback(ElevatorEffectService::loadEffects);

        ElevatorEffectService.initialized = true;
    }

    private static void loadEffects(ConfigRoot rootNode) {
        elevatorEffects.clear();

        File effectDirectory = new File(Elevators.getConfigDirectory(), "effects");
        ResourceHelper.exportResource(Elevators.getInstance(), "Creeper.png", new File(effectDirectory, "Creeper.png"), false);

        elevatorEffects.put("ARROW", new ArrowEffect());
        elevatorEffects.put("HELIX", new HelixEffect());
        // elevatorEffects.put("SPARKLES", new SparklesEffect());

        Map<String, ConfigEffect> effectConfigs = ElevatorConfigService.getEffectConfigs();
        for(String elevatorEffectKey : effectConfigs.keySet()) {
            ConfigEffect effectConfig = effectConfigs.get(elevatorEffectKey);
            elevatorEffectKey = elevatorEffectKey.toUpperCase();

            File effectFile = new File(effectDirectory, effectConfig.file);
            if (!effectFile.exists()) {
                Elevators.getElevatorsLogger().warning("Elevators: Could not find file for effect \"" + elevatorEffectKey + "\"");
                continue;
            }

            elevatorEffects.put(elevatorEffectKey, new ImageEffect(elevatorEffectKey, effectFile, effectConfig.scale, effectConfig.duration, effectConfig.useHolo, effectConfig.background));
        }

    }

    public static ElevatorEffect getEffectFromKey(String effectKey) {
        effectKey = effectKey.toUpperCase();
        if(!elevatorEffects.containsKey(effectKey))
            return elevatorEffects.get("NONE");

        return elevatorEffects.get(effectKey);
    }

    public static List<ElevatorEffect> getEffects() {
        return new ArrayList<>(elevatorEffects.values());
    }

}
