package com.lkeehl.elevators.services;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.settings.*;
import com.lkeehl.elevators.services.configs.ConfigRoot;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ElevatorSettingService {

    private static boolean initialized = false;

    private static final Map<Class<? extends ElevatorSetting<?>>, ElevatorSetting<?>> settingsMap = new HashMap<>();

    public static void init() {
        if(ElevatorSettingService.initialized)
            return;

        ElevatorConfigService.addConfigCallback(ElevatorSettingService::registerDefaultSettings);

        ElevatorSettingService.initialized = true;
    }

    private static void registerDefaultSettings(ConfigRoot config) {
        settingsMap.clear();

        addSetting(CanExplodeSetting.class);
        addSetting(CheckColorSetting.class);
        addSetting(CheckPermsSetting.class);
        addSetting(ClassCheckSetting.class);
        addSetting(DisplayNameSetting.class);
        addSetting(LoreLinesSetting.class);
        addSetting(MaxDistanceSetting.class);
        addSetting(MaxSolidBlocksSetting.class);
        addSetting(MaxStackSizeSetting.class);
        addSetting(StopObstructionSetting.class);
        addSetting(SupportDyingSetting.class);
        addSetting(HologramLinesSetting.class);
    }

    public static void addSetting(Class<? extends ElevatorSetting<?>> settingsClass) {
        try {
            Constructor<?> constructor = settingsClass.getConstructor();
            settingsMap.put(settingsClass, (ElevatorSetting<?>) constructor.newInstance());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getSettingValue(Elevator elevator, Class<? extends ElevatorSetting<T>> settingsClass) {

        if(!settingsMap.containsKey(settingsClass))
            throw new RuntimeException("Settings class not found");

        return (T) settingsMap.get(settingsClass).getIndividualElevatorValue(elevator);
    }

    public static <T> T getSettingValue(ElevatorType elevatorType, Class<? extends ElevatorSetting<T>> settingsClass) {

        if(!settingsMap.containsKey(settingsClass))
            throw new RuntimeException("Settings class not found");

        return (T) settingsMap.get(settingsClass).getCurrentValueGlobal(elevatorType);
    }

    public static <T extends ElevatorSetting<Z>,Z> T getElevatorSetting(Class<T> settingsClass) {

        if(!settingsMap.containsKey(settingsClass))
            throw new RuntimeException("Settings class not found");

        return (T) settingsMap.get(settingsClass);
    }

    public static List<ElevatorSetting<?>> getElevatorSettings() {
        return new ArrayList<>(settingsMap.values());
    }



}
