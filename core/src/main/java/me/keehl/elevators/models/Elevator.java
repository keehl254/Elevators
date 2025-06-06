package me.keehl.elevators.models;

import me.keehl.elevators.services.ElevatorTypeService;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;

public class Elevator {

    private final ShulkerBox shulkerBox;

    private final ElevatorType elevatorType;

    public Elevator(ShulkerBox shulkerBox, ElevatorType elevatorType) {
        this.shulkerBox = shulkerBox;
        this.elevatorType = elevatorType;
    }

    public ShulkerBox getShulkerBox() {
        return this.shulkerBox;
    }

    public ElevatorType getElevatorType() {
        return this.elevatorType;
    }

    public ElevatorType getElevatorType(boolean useSnapshot) {
        if(useSnapshot)
            return this.getElevatorType();
        return ElevatorTypeService.getElevatorType(this.elevatorType.getTypeKey());
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
