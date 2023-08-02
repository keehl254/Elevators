package com.lkeehl.elevators.models;

import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;

public class ElevatorEventData {

    private final ShulkerBox origin;
    private final ElevatorType elevatorType;
    private final ShulkerBox destination;
    private final byte direction;
    private final double standOnAddition;

    public ElevatorEventData(ShulkerBox origin, ElevatorType elevatorType, ShulkerBox destination, byte direction, double standOnAddition) {
        this.origin = origin;
        this.elevatorType = elevatorType;
        this.destination = destination;
        this.direction = direction;
        this.standOnAddition = standOnAddition;
    }

    public ElevatorEventData(ShulkerBox origin, ElevatorType elevatorType, ShulkerBox destination) {
        this(origin, elevatorType, destination, (byte) 1, 0.0);
    }

    public ElevatorEventData(ElevatorType elevatorType) {
        this(null, elevatorType, null);
    }

    public ShulkerBox getOrigin() {
        return this.origin;
    }

    public ElevatorType getElevatorType() { return this.elevatorType;}

    public ShulkerBox getDestination() {
        return this.destination;
    }

    public byte getDirection() {
        return this.direction;
    }

    public double getStandOnAddition() {
        return this.standOnAddition;
    }

}
