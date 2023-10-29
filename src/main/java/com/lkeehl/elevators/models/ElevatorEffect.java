package com.lkeehl.elevators.models;

import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;

public abstract class ElevatorEffect {

    private String effectKey;

    public ElevatorEffect(String effectKey) {
        this.effectKey = effectKey;
    }

    protected Location getEffectLocation(ElevatorEventData teleportResult, ExecutionMode executionMode) {
        if (executionMode == ExecutionMode.DESTINATION)
            return teleportResult.getDestination().getLocation().clone();
        return teleportResult.getOrigin().getLocation().clone();
    }

    private Color extractColorFromDyeColor(DyeColor dyeColor) {
        return dyeColor == null ? Color.WHITE : dyeColor.getColor();
    }

    protected Color getParticleColor(ElevatorEventData teleportResult) {
        if (ConfigService.getRootConfig().effectDestination == ExecutionMode.DESTINATION)
            return this.extractColorFromDyeColor(teleportResult.getDestination().getColor());
        return this.extractColorFromDyeColor(teleportResult.getOrigin().getColor());
    }

    public String getEffectKey() {
        return this.effectKey;
    }

    public abstract void playEffect(ElevatorEventData teleportResult, ExecutionMode executionMode);

    public void playEffect(ElevatorEventData teleportResult) {
        ExecutionMode.executeConsumerWithMode(ConfigService.getRootConfig().effectDestination, i->i, executionMode -> playEffect(teleportResult, executionMode));
    }

}
