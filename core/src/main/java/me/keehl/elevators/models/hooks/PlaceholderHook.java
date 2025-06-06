package me.keehl.elevators.models.hooks;

import org.bukkit.entity.Player;

public abstract class PlaceholderHook implements ElevatorHook {

    public abstract String formatPlaceholders(Player player, String message);

}