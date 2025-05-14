package com.lkeehl.elevators.models.hooks;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.ShulkerBoxHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ElevatorHologramService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;

import java.util.List;
import java.util.function.Consumer;

public abstract class WrappedHologram {

    private final Location elevatorLocation;
    private final Consumer<WrappedHologram> deleteConsumer;

    public WrappedHologram(Location location, Consumer<WrappedHologram> deleteConsumer) {
        this.elevatorLocation = location;
        this.deleteConsumer = deleteConsumer;
    }

    public abstract void addLine(String text);

    public abstract void setLines(List<String> text);

    public abstract void clearLines();

    public abstract double getHeight();

    public abstract void teleportTo(Location location);

    public Location getElevatorLocation() {
        return this.elevatorLocation;
    }

    public Elevator getElevator() {
        Block block = this.elevatorLocation.getBlock();
        ShulkerBox box = ShulkerBoxHelper.getShulkerBox(block);
        if(box == null)
            return null;
        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box);
        if(elevatorType == null)
            return null;

        return new Elevator(box, elevatorType);
    }

    public void update() {

        if(!this.getElevatorLocation().getChunk().isLoaded())
            return;

        ElevatorHologramService.updateElevatorHologram(this.getElevator());
    }

    public final void delete() {
        // Elevators.getElevatorsLogger().warning("Deleting hologram at " + this.elevatorLocation.getBlockX() + " " + this.elevatorLocation.getBlockY() + " " + this.elevatorLocation.getBlockZ());
        this.onDelete();
        this.deleteConsumer.accept(this);
    }

    public abstract void onDelete();

}
