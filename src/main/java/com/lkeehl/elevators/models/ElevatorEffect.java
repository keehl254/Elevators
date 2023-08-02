package com.lkeehl.elevators.models;

import com.lkeehl.elevators.helpers.ShulkerBoxHelper;
import com.lkeehl.elevators.services.ConfigService;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;

public abstract class ElevatorEffect {

    private String effectKey;

    public ElevatorEffect(String effectKey) {
        this.effectKey = effectKey;
    }

    protected Location getEffectLocation(ElevatorEventData teleportResult) {
        if(ConfigService.getRootConfig().playEffectAtDestination)
            return teleportResult.getDestination().getLocation();

        return teleportResult.getOrigin().getLocation().clone();
    }

    private Color extractColorFromDyeColor(DyeColor dyeColor) {
        return dyeColor == null ? Color.WHITE : dyeColor.getColor();
    }

    protected Color getParticleColor(ElevatorEventData teleportResult) {
        if(ConfigService.getRootConfig().playEffectAtDestination)
            return this.extractColorFromDyeColor(teleportResult.getDestination().getColor());
        return this.extractColorFromDyeColor(teleportResult.getOrigin().getColor());
    }

    public String getEffectKey() {
        return this.effectKey;
    }

    public abstract void playEffect(ElevatorEventData teleportResult);

}
