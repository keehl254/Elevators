package com.lkeehl.elevators.models;

import org.bukkit.block.ShulkerBox;

public class ElevatorSearchResult {

    private final double standOnAddition;

    private final ShulkerBox destination;

    public ElevatorSearchResult(ShulkerBox destination, double standOnAddition) {
        this.destination = destination;
        this.standOnAddition = standOnAddition;
    }

    public ShulkerBox getDestination() {
        return this.destination;
    }

    public double getStandOnAddition() {
        return this.standOnAddition;
    }

}
