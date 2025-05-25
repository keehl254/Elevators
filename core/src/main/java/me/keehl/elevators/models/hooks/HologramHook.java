package me.keehl.elevators.models.hooks;

import me.keehl.elevators.models.Elevator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.function.Consumer;

public abstract class HologramHook<HOLO extends WrappedHologram> implements ElevatorHook {


    public abstract HOLO createHologram(Elevator elevator, Consumer<WrappedHologram> deleteConsumer, String... lines);


    public abstract void clearAll();

    public abstract HOLO getHologram(String uuid);

    public abstract Collection<HOLO> getHolograms();

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        return null;
    }

}
