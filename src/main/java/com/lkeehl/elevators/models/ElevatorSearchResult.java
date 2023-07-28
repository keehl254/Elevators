package com.lkeehl.elevators.models;

import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;

public class ElevatorSearchResult {

    private final Location originLocation;
    private final ElevatorType elevatorType;
    private final ShulkerBox destination;
    private final byte direction;
    private final double standOnAddition;

    public ElevatorSearchResult(Location originLocation, ElevatorType elevatorType, ShulkerBox destination, byte direction, double standOnAddition) {
        this.originLocation = originLocation;
        this.elevatorType = elevatorType;
        this.destination = destination;
        this.direction = direction;
        this.standOnAddition = standOnAddition;
    }

    public Location getOriginLocation() {
        return this.originLocation;
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
