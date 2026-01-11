package me.keehl.elevators.api.models;

import me.keehl.elevators.api.util.ExecutionMode;
import org.bukkit.entity.Player;

public interface IElevatorEventData {

    Player getPlayer();
    IElevator getOrigin();
    IElevator getDestination();

    byte getDirection();

    double getStandOnAddition();

    IElevator getElevatorFromExecutionMode(ExecutionMode executionMode);

}
