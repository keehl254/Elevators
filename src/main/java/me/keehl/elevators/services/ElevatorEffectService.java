package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.IElevatorEffect;
import me.keehl.elevators.api.services.IElevatorEffectsService;
import me.keehl.elevators.api.services.configs.versions.IConfigEffect;
import me.keehl.elevators.api.services.configs.versions.IConfigRoot;
import me.keehl.elevators.effects.ArrowEffect;
import me.keehl.elevators.effects.HelixEffect;
import me.keehl.elevators.effects.ImageEffect;
import me.keehl.elevators.helpers.ResourceHelper;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ElevatorEffectService extends ElevatorService implements IElevatorEffectsService {

    private boolean initialized = false;

    private final Map<String, IElevatorEffect> elevatorEffects = new HashMap<>();

    public ElevatorEffectService(IElevators elevators) {
        super(elevators);
    }

    @Override
    public void onInitialize() {
        if(this.initialized)
            return;
        ElevatorsAPI.pushAndHoldLog();

        Elevators.getConfigService().addConfigCallback(this::loadEffects);

        this.initialized = true;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Effect service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    @Override
    public void onUninitialize() {

    }

    private void loadEffects(IConfigRoot rootNode) {

        ElevatorsAPI.pushAndHoldLog();

        this.elevatorEffects.clear();

        File effectDirectory = new File(Elevators.getConfigDirectory(), "effects");
        ResourceHelper.exportResource(Elevators.getInstance(), "Creeper.png", new File(effectDirectory, "Creeper.png"), false);

        this.elevatorEffects.put("ARROW", new ArrowEffect());
        this.elevatorEffects.put("HELIX", new HelixEffect());
        // elevatorEffects.put("SPARKLES", new SparklesEffect());

        Map<String, IConfigEffect> effectConfigs = Elevators.getConfigService().getEffectConfigs();
        for(String elevatorEffectKey : effectConfigs.keySet()) {
            IConfigEffect effectConfig = effectConfigs.get(elevatorEffectKey);
            elevatorEffectKey = elevatorEffectKey.toUpperCase();

            File effectFile = new File(effectDirectory, effectConfig.getFile());
            if (!effectFile.exists()) {
                ElevatorsAPI.log(Level.WARNING, "Elevators: Could not find file for effect \"" + elevatorEffectKey + "\"");
                continue;
            }

            this.elevatorEffects.put(elevatorEffectKey, new ImageEffect(elevatorEffectKey, effectFile, effectConfig.getScale(), effectConfig.getDuration(), effectConfig.getUseHolo(), effectConfig.getBackground()));
        }

        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Registered " + this.elevatorEffects.size() + " effects. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

    }

    @Override
    public IElevatorEffect getEffectFromKey(String effectKey) {
        return this.elevatorEffects.getOrDefault(effectKey.toUpperCase(), null);
    }

    @Override
    public void registerVisualEffect(IElevatorEffect effect) {
        this.elevatorEffects.put(effect.getEffectKey(), effect);
    }

    @Override
    public List<IElevatorEffect> getEffects() {
        return new ArrayList<>(this.elevatorEffects.values());
    }

}
