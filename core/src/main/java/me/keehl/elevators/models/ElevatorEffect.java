package me.keehl.elevators.models;

import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.util.ExecutionMode;
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
        ExecutionMode.executeConsumerWithMode(executionMode, teleportResult::getElevatorFromExecutionMode, elevator -> this.playEffect(teleportResult, elevator));
    }

}
