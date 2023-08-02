package com.lkeehl.elevators.models.hooks;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class HologramHook<HOLO extends WrappedHologram> implements ElevatorHook {

    public HOLO createHologram(Location location, String... lines) {
        return createHologram(location,0.0,lines);
    }

    public abstract HOLO createHologram(Location location, double raise, String... lines);


    public abstract void clearAll();

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        return null;
    }

}
