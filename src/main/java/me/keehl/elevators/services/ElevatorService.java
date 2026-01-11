package me.keehl.elevators.services;

import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.services.IElevatorService;

public abstract class ElevatorService implements IElevatorService {

    private final IElevators elevators;

    public ElevatorService(IElevators elevators) {
        this.elevators = elevators;
    }

    public IElevators getElevators() {
        return this.elevators;
    }

}
