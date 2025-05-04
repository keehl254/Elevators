package com.lkeehl.elevators.models;

import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.services.ElevatorConfigService;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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

    protected Location getEffectLocation(Elevator elevator) {
        return elevator.getLocation().clone();
    }

    private Color extractColorFromDyeColor(DyeColor dyeColor) {
        return dyeColor == null ? Color.WHITE : dyeColor.getColor();
    }

    protected Color getParticleColor(Elevator elevator) {
        return this.extractColorFromDyeColor(elevator.getDyeColor());
    }

    public String getEffectKey() {
        return this.effectKey;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public abstract void playEffect(ElevatorEventData teleportResult, Elevator elevator);

    public void playEffect(ElevatorEventData teleportResult) {
        ExecutionMode executionMode = ElevatorConfigService.getRootConfig().effectDestination;

        List<Elevator> effectElevators;
        if(executionMode == ExecutionMode.BOTH)
            effectElevators = List.of(teleportResult.getDestination(), teleportResult.getOrigin());
        else if(executionMode == ExecutionMode.ORIGIN)
            effectElevators = List.of(teleportResult.getOrigin());
        else
            effectElevators = List.of(teleportResult.getDestination());

        for(Elevator elevator : effectElevators)
            this.playEffect(teleportResult, elevator);
    }

}
