package com.lkeehl.elevators.services;

import com.lkeehl.elevators.models.ElevatorType;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ElevatorTypeService {

    private static final Map<String, ElevatorType> elevatorTypes = new HashMap<>();

    private static ElevatorType defaultElevatorType;

    private static boolean initialized = false;

    public static void init() {
        if(ElevatorTypeService.initialized)
            return;

        ConfigService.addConfigCallback(ElevatorTypeService::reloadElevatorsFromConfig);

        ElevatorTypeService.initialized = true;
    }

    private static void reloadElevatorsFromConfig(CommentedConfigurationNode config) {
        elevatorTypes.clear();
        // TODO: Clear elevator recipes


        // TODO: Register the default elevator
    }

    public static ElevatorType getElevatorType(String name) {
        return elevatorTypes.getOrDefault(name.toUpperCase(), null);
    }

    public static ElevatorType getDefaultElevatorType() {
        return defaultElevatorType;
    }

    public static boolean doesElevatorTypeExist(String name) {
        return elevatorTypes.containsKey(name.toUpperCase());
    }

    public static Collection<ElevatorType> getExistingElevatorTypes() {
        return elevatorTypes.values();
    }

    public static Set<String> getExistingElevatorKeys() {
        return elevatorTypes.keySet();
    }

    public static void registerElevatorType(ElevatorType elevatorType) {
        elevatorTypes.put(elevatorType.getTypeName(), elevatorType);
    }

    public static void unregisterElevatorType(ElevatorType elevatorType) {
        elevatorTypes.remove(elevatorType.getTypeName());
    }

}
