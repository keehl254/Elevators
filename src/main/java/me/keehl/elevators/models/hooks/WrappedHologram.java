package me.keehl.elevators.models.hooks;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.hooks.IElevatorHologram;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.hooks.IWrappedHologram;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.List;
import java.util.UUID;

public class WrappedHologram implements IWrappedHologram {

    private final String uuid;
    private final Location elevatorLocation;
    private final String elevatorTypeKey;

    private final IElevatorHologram wrappedHologram;

    public WrappedHologram(UUID uuid, IElevatorHologram wrappedHologram, IElevator elevator) {
        this.uuid = uuid.toString();
        this.wrappedHologram = wrappedHologram;
        this.elevatorLocation = elevator.getLocation();
        this.elevatorTypeKey = elevator.getElevatorType().getTypeKey(); // Store Elevator Type Key to account for a config reload.

        Elevators.getHologramService().registerHologram(this);
    }

    public void addLine(String text) {
        this.wrappedHologram.addLine(text);
    }

    public void setLines(List<String> text) {
        this.wrappedHologram.setLines(text);
    }

    public double getHeight() {
        return this.wrappedHologram.getHeight();
    }

    public void teleportTo(Location location) {
        this.wrappedHologram.teleportTo(location);
    }

    public String getUUID() {
        return this.uuid;
    }

    public Location getElevatorLocation() {
        return this.elevatorLocation;
    }

    public IElevatorType getElevatorType() {
        return Elevators.getElevatorTypeService().getElevatorType(this.elevatorTypeKey);
    }

    public IElevator getElevatorRecord() {
        Block block = this.elevatorLocation.getBlock();
        return ElevatorsAPI.createElevatorRecord(block);
    }

    public void update() {

        if(!this.getElevatorLocation().getChunk().isLoaded())
            return;

        IElevator elevator = this.getElevatorRecord();
        if(elevator == null) {
            this.delete();
            return;
        }

        Elevators.getHologramService().updateElevatorHologram(this.getElevatorRecord());
    }

    public final void delete() {
        this.wrappedHologram.onDelete();
        Elevators.getHologramService().unregisterHologram(this);
    }

}
