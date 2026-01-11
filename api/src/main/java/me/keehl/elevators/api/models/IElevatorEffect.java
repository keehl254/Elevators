package me.keehl.elevators.api.models;

import org.bukkit.inventory.ItemStack;

public interface IElevatorEffect {

    String getEffectKey();

    ItemStack getIcon();

    void playEffect(IElevatorEventData teleportResult, IElevator elevator);

}
