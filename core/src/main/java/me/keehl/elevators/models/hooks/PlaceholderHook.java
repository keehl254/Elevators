package me.keehl.elevators.models.hooks;

import me.keehl.elevators.models.Elevator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PlaceholderHook implements ElevatorHook {

    public abstract String formatPlaceholders(Player player, String message);

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        return null;
    }

}