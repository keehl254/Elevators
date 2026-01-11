package me.keehl.elevators.api.services;

import me.keehl.elevators.api.models.IElevatorType;

import java.util.*;

public interface IElevatorTypeService extends IElevatorService {

    IElevatorType getElevatorType(String name);

    IElevatorType getDefaultElevatorType();

    boolean doesElevatorTypeExist(String name);

    Collection<IElevatorType> getExistingElevatorTypes();

    Set<String> getExistingElevatorKeys();

    IElevatorType createElevatorType(String typeKey);
}
