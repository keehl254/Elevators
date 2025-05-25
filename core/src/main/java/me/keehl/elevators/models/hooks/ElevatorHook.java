package me.keehl.elevators.models.hooks;

import me.keehl.elevators.models.Elevator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ElevatorHook {

    boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage);

    ItemStack createIconForElevator(Player player, Elevator elevator);

}
