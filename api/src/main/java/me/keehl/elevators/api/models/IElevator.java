package me.keehl.elevators.api.models;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;

public interface IElevator {

    ShulkerBox getShulkerBox();

    IElevatorType getElevatorType();

    IElevatorType getElevatorType(boolean useSnapshot);

    Location getLocation();

    DyeColor getDyeColor();

    boolean isValid();

}
