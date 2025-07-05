package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.configs.versions.configv5_1_0.ConfigRoot;
import org.bukkit.ChatColor;

import java.util.*;

public class ElevatorTypeService {

    private static ElevatorType defaultElevatorType;

    private static boolean initialized = false;

    public static void init() {
        if(ElevatorTypeService.initialized)
            return;
        Elevators.pushAndHoldLog();

        ElevatorConfigService.addConfigCallback(ElevatorTypeService::reloadElevatorsFromConfig);

        ElevatorTypeService.initialized = true;
        Elevators.popLog(logData -> Elevators.log("Type service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    private static void reloadElevatorsFromConfig(ConfigRoot config) {
        Elevators.pushAndHoldLog();

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

            Elevators.log("No DEFAULT Elevator Type found. Registering new.");
        }

        defaultElevatorType = elevatorTypes.get("DEFAULT");

        for(String elevatorKey : elevatorTypes.keySet()) {
            ElevatorType elevatorType = elevatorTypes.get(elevatorKey);
            elevatorType.setKey(elevatorKey);
            elevatorType.onLoad();
        }

        Elevators.popLog(logData -> Elevators.log("Registered and loaded " + elevatorTypes.size() + " elevator types. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
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

        return getElevatorType(typeKey);
    }

    public static void unregisterElevatorType(ElevatorType elevatorType) {
        ElevatorConfigService.getElevatorTypeConfigs().remove(elevatorType.getTypeKey());
        reloadElevatorsFromConfig(ElevatorConfigService.getRootConfig());
    }

}
