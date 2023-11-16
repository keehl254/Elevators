package com.lkeehl.elevators.models;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;

public class Elevator {

    private ShulkerBox shulkerBox;

    private ElevatorType elevatorType;

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

    public Location getLocation() {
        return this.shulkerBox.getLocation();
    }

    public DyeColor getDyeColor() {
        DyeColor dyeColor = this.shulkerBox.getColor();
        return dyeColor == null ? DyeColor.BLACK : dyeColor;
    }

}
