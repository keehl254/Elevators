package me.keehl.elevators.models.hooks;

import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.ShulkerBoxHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorHologramService;
import me.keehl.elevators.services.ElevatorTypeService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;

import java.util.List;

public abstract class WrappedHologram {

    private final String uuid;
    private final Location elevatorLocation;
    private final String elevatorTypeKey;

    public WrappedHologram(Elevator elevator) {

        this.uuid = ElevatorHologramService.getNextAvailableUUID().toString();
        this.elevatorLocation = elevator.getLocation();
        this.elevatorTypeKey = elevator.getElevatorType().getTypeKey(); // Store Elevator Type Key to account for a config reload.

        ElevatorHologramService.registerHologram(this);
    }

    public abstract void addLine(String text);

    public abstract void setLines(List<String> text);

    public abstract double getHeight();

    public abstract void teleportTo(Location location);

    public String getUUID() {
        return this.uuid;
    }

    public Location getElevatorLocation() {
        return this.elevatorLocation;
    }

    public ElevatorType getElevatorType() {
        return ElevatorTypeService.getElevatorType(this.elevatorTypeKey);
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

        Elevator elevator = this.getElevator();
        if(elevator == null) {
            this.delete();
            return;
        }

        ElevatorHologramService.updateElevatorHologram(this.getElevator());
    }

    public final void delete() {
        this.onDelete();
        ElevatorHologramService.unregisterHologram(this);
    }

    public abstract void onDelete();

}
