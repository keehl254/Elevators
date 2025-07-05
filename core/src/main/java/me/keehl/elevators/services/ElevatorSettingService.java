package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.settings.*;
import me.keehl.elevators.services.configs.versions.configv5_1_0.ConfigRoot;
import org.bukkit.ChatColor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ElevatorSettingService {

    private static boolean initialized = false;

    private static final Map<Class<? extends ElevatorSetting<?>>, ElevatorSetting<?>> settingsMap = new HashMap<>();

    public static void init() {
        if(ElevatorSettingService.initialized)
            return;
        Elevators.pushAndHoldLog();

        ElevatorConfigService.addConfigCallback(ElevatorSettingService::registerDefaultSettings);

        ElevatorSettingService.initialized = true;
        Elevators.popLog(logData -> Elevators.log("Setting service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    private static void registerDefaultSettings(ConfigRoot config) {

        Elevators.pushAndHoldLog();

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

        Elevators.popLog(logData -> Elevators.log("Registered " + settingsMap.size() + " settings. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
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
