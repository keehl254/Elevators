package com.lkeehl.elevators.services;

import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.configs.ConfigElevatorType;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.*;
import java.util.stream.Collectors;

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

        Map<String, ConfigElevatorType> elevatorTypeConfigs = ConfigService.getElevatorTypeConfigs();
        elevatorTypes.put("DEFAULT", ElevatorTypeService.createElevatorFromConfig("DEFAULT", new ConfigElevatorType()));

        for(String elevatorTypeKey : elevatorTypeConfigs.keySet())
            elevatorTypes.put(elevatorTypeKey.toUpperCase(), ElevatorTypeService.createElevatorFromConfig(elevatorTypeKey.toUpperCase(), elevatorTypeConfigs.get(elevatorTypeKey)));

        defaultElevatorType = elevatorTypes.get("DEFAULT");
    }

    private static ElevatorType createElevatorFromConfig(String elevatorTypeKey, ConfigElevatorType config) {
        ElevatorType elevatorType = new ElevatorType(elevatorTypeKey);

        elevatorType.setDisplayName(config.displayName);
        elevatorType.setMaxDistanceAllowedBetweenElevators(config.maxDistance);
        elevatorType.setMaxSolidBlocksAllowedBetweenElevators(config.maxSolidBlocks);
        elevatorType.setMaxStackSize(config.maxStackSize);
        elevatorType.getLore().addAll(config.loreLines);
        elevatorType.setCanRecipesProduceColor(config.coloredOutput);
        elevatorType.setCanTeleportToOtherColor(config.checkColor);
        elevatorType.setCheckDestinationElevatorType(config.classCheck);
        elevatorType.setBlocksObstruction(config.stopObstruction);
        elevatorType.setElevatorRequiresPermissions(config.checkPerms);
        elevatorType.setCanElevatorExplode(config.canExplode);

        elevatorType.getActionsUp().addAll(config.actions.up.stream().map(i->ElevatorActionService.createActionFromString(elevatorType, i)).filter(Objects::nonNull).collect(Collectors.toList()));
        elevatorType.getActionsDown().addAll(config.actions.down.stream().map(i->ElevatorActionService.createActionFromString(elevatorType, i)).filter(Objects::nonNull).collect(Collectors.toList()));


        return elevatorType;
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
        elevatorTypes.put(elevatorType.getTypeKey(), elevatorType);
    }

    public static void unregisterElevatorType(ElevatorType elevatorType) {
        elevatorTypes.remove(elevatorType.getTypeKey());
    }

}
