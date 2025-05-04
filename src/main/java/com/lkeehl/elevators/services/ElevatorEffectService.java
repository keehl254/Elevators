package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.effects.ArrowEffect;
import com.lkeehl.elevators.effects.HelixEffect;
import com.lkeehl.elevators.effects.ImageEffect;
import com.lkeehl.elevators.helpers.ResourceHelper;
import com.lkeehl.elevators.models.ElevatorEffect;
import com.lkeehl.elevators.services.configs.ConfigEffect;
import com.lkeehl.elevators.services.configs.ConfigRoot;

import java.io.File;
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
        return elevatorEffects.values().stream().toList();
    }

}
