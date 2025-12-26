package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.effects.ArrowEffect;
import me.keehl.elevators.effects.HelixEffect;
import me.keehl.elevators.effects.ImageEffect;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.models.ElevatorEffect;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigEffect;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigRoot;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ElevatorEffectService {

    private static boolean initialized = false;

    private static final Map<String, ElevatorEffect> elevatorEffects = new HashMap<>();

    public static void init() {
        if(ElevatorEffectService.initialized)
            return;
        Elevators.pushAndHoldLog();

        ElevatorConfigService.addConfigCallback(ElevatorEffectService::loadEffects);

        ElevatorEffectService.initialized = true;
        Elevators.popLog(logData -> Elevators.log("Effect service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    private static void loadEffects(ConfigRoot rootNode) {

        Elevators.pushAndHoldLog();

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
                Elevators.log(Level.WARNING, "Elevators: Could not find file for effect \"" + elevatorEffectKey + "\"");
                continue;
            }

            elevatorEffects.put(elevatorEffectKey, new ImageEffect(elevatorEffectKey, effectFile, effectConfig.scale, effectConfig.duration, effectConfig.useHolo, effectConfig.background));
        }

        Elevators.popLog(logData -> Elevators.log("Registered " + elevatorEffects.size() + " effects. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

    }

    public static ElevatorEffect getEffectFromKey(String effectKey) {
        return elevatorEffects.getOrDefault(effectKey.toUpperCase(), null);
    }

    public static void registerVisualEffect(ElevatorEffect effect) {
        elevatorEffects.put(effect.getEffectKey(), effect);
    }

    public static List<ElevatorEffect> getEffects() {
        return new ArrayList<>(elevatorEffects.values());
    }

}
