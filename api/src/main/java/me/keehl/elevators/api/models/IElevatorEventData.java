package me.keehl.elevators.api.models;

import me.keehl.elevators.api.util.ExecutionMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface IElevatorEventData {

    Player getPlayer();
    @Nullable IElevator getOrigin();
    @Nullable IElevator getDestination();

    IElevatorType getElevatorType();

    byte getDirection();

    double getStandOnAddition();

    IElevator getElevatorFromExecutionMode(ExecutionMode executionMode);

}
