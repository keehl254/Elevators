package com.lkeehl.elevators.services;

import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.configs.versions.configv5.ConfigRoot;

import java.util.*;

public class ElevatorTypeService {

    private static ElevatorType defaultElevatorType;

    private static boolean initialized = false;

    public static void init() {
        if(ElevatorTypeService.initialized)
            return;

        ElevatorConfigService.addConfigCallback(ElevatorTypeService::reloadElevatorsFromConfig);

        ElevatorTypeService.initialized = true;
    }

    private static void reloadElevatorsFromConfig(ConfigRoot config) {
        Map<String, ElevatorType> elevatorTypes = ElevatorConfigService.getElevatorTypeConfigs();
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
        return ElevatorConfigService.getElevatorTypeConfigs().getOrDefault(name.toUpperCase(), null);
    }

    public static ElevatorType getDefaultElevatorType() {
        return defaultElevatorType;
    }

    public static boolean doesElevatorTypeExist(String name) {
        return ElevatorConfigService.getElevatorTypeConfigs().containsKey(name.toUpperCase());
    }

    public static Collection<ElevatorType> getExistingElevatorTypes() {
        return ElevatorConfigService.getElevatorTypeConfigs().values();
    }

    public static Set<String> getExistingElevatorKeys() {
        return ElevatorConfigService.getElevatorTypeConfigs().keySet();
    }

    public static void registerElevatorType(ElevatorType elevatorType) {
        ElevatorConfigService.getElevatorTypeConfigs().put(elevatorType.getTypeKey().toUpperCase(), elevatorType);
        reloadElevatorsFromConfig(ElevatorConfigService.getRootConfig());
    }

    public static ElevatorType createElevatorType(String typeKey) {
        typeKey = typeKey.toUpperCase();
        ElevatorType type = new ElevatorType();
        ElevatorConfigService.getElevatorTypeConfigs().put(typeKey, type);
        reloadElevatorsFromConfig(ElevatorConfigService.getRootConfig());

        return type;
    }

    public static void unregisterElevatorType(ElevatorType elevatorType) {
        ElevatorConfigService.getElevatorTypeConfigs().remove(elevatorType.getTypeKey());
        reloadElevatorsFromConfig(ElevatorConfigService.getRootConfig());
    }

}
