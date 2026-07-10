package me.keehl.elevators.models;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.ExecutionMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElevatorEventData implements IElevatorEventData {

    private final Player player;

    private final IElevatorType originElevatorType;

    private final @Nullable IElevator originElevator;
    private final @Nullable IElevator destinationElevator;

    private final byte direction;
    private final double standOnAddition;

    public ElevatorEventData(Player player, @NotNull IElevator originElevator, @Nullable IElevator destinationElevator, byte direction, double standOnAddition) {
        this.player = player;
        this.originElevatorType = originElevator.getSnapshotElevatorType();
        this.originElevator = originElevator;
        this.destinationElevator = destinationElevator;
        this.direction = direction;
        this.standOnAddition = standOnAddition;
    }

    public ElevatorEventData(Player player, IElevatorType elevatorType) {
        this.player = player;
        this.originElevatorType = elevatorType;
        this.originElevator = null;
        this.destinationElevator = null;
        this.direction = (byte) 1;
        this.standOnAddition = 0.0;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    public @Nullable IElevator getOrigin() { return this.originElevator;}
    public @Nullable IElevator getDestination() { return this.destinationElevator;}

    public IElevatorType getElevatorType() {
        if(this.originElevator != null)
            return this.originElevator.getSnapshotElevatorType();

        return this.originElevatorType;
    }

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
