package com.lkeehl.elevators.services;

import com.lkeehl.elevators.effects.ArrowEffect;
import com.lkeehl.elevators.effects.HelixEffect;
import com.lkeehl.elevators.models.ElevatorEffect;
import com.lkeehl.elevators.services.configs.ConfigEffect;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.HashMap;
import java.util.Map;

public class ElevatorEffectService {

    private static boolean initialized = false;

    private static final Map<String, ElevatorEffect> elevatorEffects = new HashMap<>();

    public static void init() {
        if(ElevatorEffectService.initialized)
            return;

        ConfigService.addConfigCallback(ElevatorEffectService::loadEffects);

        ElevatorEffectService.initialized = true;
    }

    private static void loadEffects(CommentedConfigurationNode rootNode) {
        elevatorEffects.clear();

        elevatorEffects.put("ARROW", new ArrowEffect());
        elevatorEffects.put("HELIX", new HelixEffect());
        // elevatorEffects.put("SPARKLES", new SparklesEffect());

        Map<String, ConfigEffect> effectConfigs = ConfigService.getEffectConfigs();
        for(String effectKey : effectConfigs.keySet()) {
            effectKey = effectKey.toUpperCase();

        }

    }

}
