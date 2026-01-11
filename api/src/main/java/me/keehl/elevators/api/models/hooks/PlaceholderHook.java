package me.keehl.elevators.api.models.hooks;

import org.bukkit.entity.Player;

public interface PlaceholderHook extends ElevatorHook {

    String formatPlaceholders(Player player, String message);

}