package com.lkeehl.elevators.models;

import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class ElevatorEffect {

    private final String effectKey;

    private final ItemStack icon;

    public ElevatorEffect(String effectKey, ItemStack icon) {
        this.effectKey = effectKey;
        if (icon != null)
            this.icon = icon;
        else
            this.icon = ItemStackHelper.createItem(MessageHelper.fixEnum(effectKey), Material.FIREWORK_ROCKET, 1);
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
            return this.extractColorFromDyeColor(teleportResult.getDestination().getDyeColor());
        return this.extractColorFromDyeColor(teleportResult.getOrigin().getDyeColor());
    }

    public String getEffectKey() {
        return this.effectKey;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public abstract void playEffect(ElevatorEventData teleportResult, ExecutionMode executionMode);

    public void playEffect(ElevatorEventData teleportResult) {
        ExecutionMode.executeConsumerWithMode(ConfigService.getRootConfig().effectDestination, i->i, executionMode -> playEffect(teleportResult, executionMode));
    }

}
