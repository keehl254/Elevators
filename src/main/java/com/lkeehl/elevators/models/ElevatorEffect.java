package com.lkeehl.elevators.models;

import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ShulkerBoxHelper;
import com.lkeehl.elevators.services.ConfigService;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;

public abstract class ElevatorEffect {

    protected Location getEffectLocation(ElevatorSearchResult teleportResult) {
        if(ConfigService.getRootConfig().playEffectAtDestination)
            return teleportResult.getDestination().getLocation();

        return teleportResult.getOriginLocation().clone();
    }

    private Color extractColorFromDyeColor(DyeColor dyeColor) {
        return dyeColor == null ? Color.WHITE : dyeColor.getColor();
    }

    protected Color getParticleColor(ElevatorSearchResult teleportResult) {
        if(ConfigService.getRootConfig().playEffectAtDestination)
            return this.extractColorFromDyeColor(teleportResult.getDestination().getColor());
        BlockState blockState = teleportResult.getOriginLocation().getBlock().getState();
        if(!ShulkerBoxHelper.isShulkerBox(blockState))
            return Color.WHITE;

        return this.extractColorFromDyeColor(((ShulkerBox)blockState).getColor());
    }

    public abstract void playEffect(ElevatorSearchResult teleportResult, ElevatorType elevatorType, byte direction);

}
