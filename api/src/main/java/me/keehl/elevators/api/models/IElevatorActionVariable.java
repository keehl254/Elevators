package me.keehl.elevators.api.models;

public interface IElevatorActionVariable<T> {

    T getObjectFromString(String value, IElevatorAction action);

    String getStringFromObject(Object object);

    String getMainAlias();

    T getDefaultObject();

    boolean isGroupingAlias(String alias);

}
