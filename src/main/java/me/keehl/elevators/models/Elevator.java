package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;

public class Elevator implements IElevator {

    private final ShulkerBox shulkerBox;

    private final IElevatorType elevatorType;

    public Elevator(ShulkerBox shulkerBox, IElevatorType elevatorType) {
        this.shulkerBox = shulkerBox;
        this.elevatorType = elevatorType;
    }

    public ShulkerBox getShulkerBox() {
        return this.shulkerBox;
    }

    public IElevatorType getElevatorType() {
        return this.elevatorType;
    }

    public IElevatorType getElevatorType(boolean useSnapshot) {
        if(useSnapshot)
            return this.getElevatorType();
        return Elevators.getElevatorTypeService().getElevatorType(this.elevatorType.getTypeKey());
    }

    public Location getLocation() {
        return this.shulkerBox.getLocation();
    }

    public DyeColor getDyeColor() {
        return this.shulkerBox.getColor();
    }

    public boolean isValid() {
        return this.getLocation().getBlock().getType() == this.shulkerBox.getType() && this.elevatorType == getElevatorType(false);
    }

}
