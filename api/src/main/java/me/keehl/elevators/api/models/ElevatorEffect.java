package me.keehl.elevators.api.models;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public abstract class ElevatorEffect implements IElevatorEffect {

    private final String effectKey;

    private final ItemStack icon;

    public ElevatorEffect(String effectKey, ItemStack icon) {
        this.effectKey = effectKey.toUpperCase();
        this.icon = icon;
    }

    protected Location getEffectLocation(IElevator elevator) {
        return elevator.getLocation().clone();
    }

    private Color extractColorFromDyeColor(DyeColor dyeColor) {
        return dyeColor == null ? Color.WHITE : dyeColor.getColor();
    }

    protected Color getParticleColor(IElevator elevator) {
        return this.extractColorFromDyeColor(elevator.getDyeColor());
    }

    public String getEffectKey() {
        return this.effectKey;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public abstract void playEffect(IElevatorEventData teleportResult, IElevator elevator);

}
