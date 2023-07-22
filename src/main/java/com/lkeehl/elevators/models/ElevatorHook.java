package com.lkeehl.elevators.models;

import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ElevatorHook {

    boolean canPlayerUseElevator(Player player, ShulkerBox box, ElevatorType elevatorType);

    ItemStack createIconForElevator(Player player, ShulkerBox box, ElevatorType elevatorType);

}
