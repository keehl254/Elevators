package com.lkeehl.elevators.models.hooks;

import com.lkeehl.elevators.models.Elevator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public abstract class HologramHook<HOLO extends WrappedHologram> implements ElevatorHook {

    public HOLO createHologram(Location location, Consumer<WrappedHologram> deleteConsumer, String... lines) {
        return createHologram(location,deleteConsumer, 0.0,lines);
    }

    public abstract HOLO createHologram(Location location, Consumer<WrappedHologram> deleteConsumer, double raise, String... lines);


    public abstract void clearAll();

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        return null;
    }

}
