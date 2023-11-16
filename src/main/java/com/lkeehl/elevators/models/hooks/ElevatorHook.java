package com.lkeehl.elevators.models.hooks;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ElevatorHook {

    boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage);

    ItemStack createIconForElevator(Player player, Elevator elevator);

}
