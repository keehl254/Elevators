package me.keehl.elevators.api.services;

public interface IElevatorUpdateService extends IElevatorService {

    void checkUpdate();

    int checkResource(String channel);
}
