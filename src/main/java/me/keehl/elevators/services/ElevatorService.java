package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.services.IElevatorService;

public abstract class ElevatorService implements IElevatorService {

    private final Elevators elevators;

    public ElevatorService(Elevators elevators) {
        this.elevators = elevators;
    }

    public Elevators getElevators() {
        return this.elevators;
    }

}
