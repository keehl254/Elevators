package me.keehl.elevators.models;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.ExecutionMode;
import org.bukkit.entity.Player;

public class ElevatorEventData implements IElevatorEventData {

    private final Player player;

    private final IElevator originElevator;
    private final IElevator destinationElevator;

    private final byte direction;
    private final double standOnAddition;

    public ElevatorEventData(Player player, IElevator originElevator, IElevator destinationElevator, byte direction, double standOnAddition) {
        this.player = player;
        this.originElevator = originElevator;
        this.destinationElevator = destinationElevator;
        this.direction = direction;
        this.standOnAddition = standOnAddition;
    }

    public ElevatorEventData(Player player, IElevatorType elevatorType) {
        this(player, new Elevator(null, elevatorType), null, (byte) 1, 0.0);
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    public IElevator getOrigin() { return this.originElevator;}
    public IElevator getDestination() { return this.destinationElevator;}

    public byte getDirection() {
        return this.direction;
    }

    public double getStandOnAddition() {
        return this.standOnAddition;
    }

    public IElevator getElevatorFromExecutionMode(ExecutionMode executionMode) {
        return executionMode == ExecutionMode.DESTINATION ? this.getDestination() : this.getOrigin();
    }

}
