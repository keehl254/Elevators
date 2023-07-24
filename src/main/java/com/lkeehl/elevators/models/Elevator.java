package com.lkeehl.elevators.models;

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

}
