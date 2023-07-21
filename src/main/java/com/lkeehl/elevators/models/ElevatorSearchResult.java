package com.lkeehl.elevators.models;

import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;

public class ElevatorSearchResult {

    private final double standOnAddition;

    private final Location originLocation;

    private final ShulkerBox destination;

    public ElevatorSearchResult(Location originLocation, ShulkerBox destination, double standOnAddition) {
        this.originLocation = originLocation;
        this.destination = destination;
        this.standOnAddition = standOnAddition;
    }

    public Location getOriginLocation() {
        return this.originLocation;
    }

    public ShulkerBox getDestination() {
        return this.destination;
    }

    public double getStandOnAddition() {
        return this.standOnAddition;
    }

}
