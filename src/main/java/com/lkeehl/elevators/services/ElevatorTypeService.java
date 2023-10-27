package com.lkeehl.elevators.services;

import com.lkeehl.elevators.models.ElevatorRecipeGroup;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.configs.ConfigElevatorType;
import com.lkeehl.elevators.services.configs.ConfigRecipe;
import com.lkeehl.elevators.services.configs.ConfigRoot;

import java.util.*;

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

    private static void reloadElevatorsFromConfig(ConfigRoot config) {
        elevatorTypes.clear();
        // TODO: Clear elevator recipes

        Map<String, ConfigElevatorType> elevatorTypeConfigs = ConfigService.getElevatorTypeConfigs();
        elevatorTypes.put("DEFAULT", ElevatorTypeService.createElevatorFromConfig("DEFAULT", new ConfigElevatorType()));

        for(String elevatorTypeKey : elevatorTypeConfigs.keySet())
            elevatorTypes.put(elevatorTypeKey.toUpperCase(), ElevatorTypeService.createElevatorFromConfig(elevatorTypeKey.toUpperCase(), elevatorTypeConfigs.get(elevatorTypeKey)));

        defaultElevatorType = elevatorTypes.get("DEFAULT");
    }

    private static ElevatorType createElevatorFromConfig(String elevatorTypeKey, ConfigElevatorType config) {
        ElevatorType elevatorType = new ElevatorType(elevatorTypeKey, config);

        elevatorType.getActionsUp().addAll(config.actions.up.stream().map(i -> ElevatorActionService.createActionFromString(elevatorType, i)).filter(Objects::nonNull).toList());
        elevatorType.getActionsDown().addAll(config.actions.down.stream().map(i -> ElevatorActionService.createActionFromString(elevatorType, i)).filter(Objects::nonNull).toList());

        elevatorType.setElevatorUpEffect(ElevatorEffectService.getEffectFromKey(config.effects.up));
        elevatorType.setElevatorDownEffect(ElevatorEffectService.getEffectFromKey(config.effects.down));

        Map<String, ConfigRecipe> recipeMap = config.recipes;

        for(String recipeKey : recipeMap.keySet()) {
            ElevatorRecipeService.registerElevatorRecipeGroup(elevatorType, new ElevatorRecipeGroup(recipeKey, elevatorType, recipeMap.get(recipeKey)));
        }

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
