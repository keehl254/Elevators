package me.keehl.elevators.models;

import me.keehl.elevators.util.ExecutionMode;

public class ElevatorEventData {


    private final Elevator originElevator;
    private final Elevator destinationElevator;

    private final byte direction;
    private final double standOnAddition;

    public ElevatorEventData(Elevator originElevator, Elevator destinationElevator, byte direction, double standOnAddition) {
        this.originElevator = originElevator;
        this.destinationElevator = destinationElevator;
        this.direction = direction;
        this.standOnAddition = standOnAddition;
    }

    public ElevatorEventData(ElevatorType elevatorType) {
        this(new Elevator(null, elevatorType), null, (byte) 1, 0.0);
    }

    public Elevator getOrigin() { return this.originElevator;}
    public Elevator getDestination() { return this.destinationElevator;}

    public byte getDirection() {
        return this.direction;
    }

    public double getStandOnAddition() {
        return this.standOnAddition;
    }

    public Elevator getElevatorFromExecutionMode(ExecutionMode executionMode) {
        return executionMode == ExecutionMode.DESTINATION ? this.getDestination() : this.getOrigin();
    }

}
