package com.lkeehl.elevators.services;

import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.configs.ConfigRoot;

import java.util.*;

public class ElevatorTypeService {

    private static ElevatorType defaultElevatorType;

    private static boolean initialized = false;

    public static void init() {
        if(ElevatorTypeService.initialized)
            return;

        ConfigService.addConfigCallback(ElevatorTypeService::reloadElevatorsFromConfig);

        ElevatorTypeService.initialized = true;
    }

    private static void reloadElevatorsFromConfig(ConfigRoot config) {
        Map<String, ElevatorType> elevatorTypes = ConfigService.getElevatorTypeConfigs();
        List<String> elevatorsToFix = new ArrayList<>();
        for(String elevatorKey : elevatorTypes.keySet()) {
            if(!elevatorKey.equals(elevatorKey.toUpperCase()))
                elevatorsToFix.add(elevatorKey);
        }
        for(String elevatorKey : elevatorsToFix) {
            ElevatorType elevatorType = elevatorTypes.get(elevatorKey);
            elevatorTypes.remove(elevatorKey);
            elevatorTypes.put(elevatorKey.toUpperCase(), elevatorType);
        }

        if(!elevatorTypes.containsKey("DEFAULT")) {
            ElevatorType type = new ElevatorType();
            type.setKey("DEFAULT");
            elevatorTypes.put(type.getTypeKey(), type);
        }

        defaultElevatorType = elevatorTypes.get("DEFAULT");
    }

    public static ElevatorType getElevatorType(String name) {
        return ConfigService.getElevatorTypeConfigs().getOrDefault(name.toUpperCase(), null);
    }

    public static ElevatorType getDefaultElevatorType() {
        return defaultElevatorType;
    }

    public static boolean doesElevatorTypeExist(String name) {
        return ConfigService.getElevatorTypeConfigs().containsKey(name.toUpperCase());
    }

    public static Collection<ElevatorType> getExistingElevatorTypes() {
        return ConfigService.getElevatorTypeConfigs().values();
    }

    public static Set<String> getExistingElevatorKeys() {
        return ConfigService.getElevatorTypeConfigs().keySet();
    }

    public static void registerElevatorType(ElevatorType elevatorType) {
        ConfigService.getElevatorTypeConfigs().put(elevatorType.getTypeKey().toUpperCase(), elevatorType);
        reloadElevatorsFromConfig(ConfigService.getRootConfig());
    }

    public static ElevatorType createElevatorType(String typeKey) {
        typeKey = typeKey.toUpperCase();
        ElevatorType type = new ElevatorType();
        ConfigService.getElevatorTypeConfigs().put(typeKey, type);
        reloadElevatorsFromConfig(ConfigService.getRootConfig());

        return type;
    }

    public static void unregisterElevatorType(ElevatorType elevatorType) {
        ConfigService.getElevatorTypeConfigs().remove(elevatorType.getTypeKey());
        reloadElevatorsFromConfig(ConfigService.getRootConfig());
    }

}
