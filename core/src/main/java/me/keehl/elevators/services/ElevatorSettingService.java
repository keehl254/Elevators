package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.events.ElevatorRegisterSettingsEvent;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorSetting;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.settings.*;
import me.keehl.elevators.util.InternalElevatorSettingType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class ElevatorSettingService {

    private static boolean initialized = false;
    private static boolean allowSelfRegister = false;

    private static final List<ElevatorSetting<?>> elevatorSettings = new ArrayList<>();

    public static void init() {
        if(ElevatorSettingService.initialized)
            return;
        Elevators.pushAndHoldLog();

        ElevatorSettingService.registerDefaultSettings();

        ElevatorSettingService.initialized = true;
        Elevators.popLog(logData -> Elevators.log("Setting service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    private static void registerDefaultSettings() {

        Elevators.pushAndHoldLog();

        allowSelfRegister = true;
        addSetting(new UsePermissionSetting(Elevators.getInstance()));
        addSetting(new DyePermissionSetting(Elevators.getInstance()));
        addSetting(new CanExplodeSetting(Elevators.getInstance()));
        addSetting(new CheckColorSetting(Elevators.getInstance()));
        addSetting(new CheckPermsSetting(Elevators.getInstance()));
        addSetting(new ClassCheckSetting(Elevators.getInstance()));
        addSetting(new DisplayNameSetting(Elevators.getInstance()));
        addSetting(new LoreLinesSetting(Elevators.getInstance()));
        addSetting(new MaxDistanceSetting(Elevators.getInstance()));
        addSetting(new MaxSolidBlocksSetting(Elevators.getInstance()));
        addSetting(new MaxStackSizeSetting(Elevators.getInstance()));
        addSetting(new StopObstructionSetting(Elevators.getInstance()));
        addSetting(new SupportDyingSetting(Elevators.getInstance()));
        addSetting(new AllowIndividualEditSetting(Elevators.getInstance()));
        addSetting(new HologramLinesSetting(Elevators.getInstance()));

        Elevators.popLog(logData -> Elevators.log("Registered " + elevatorSettings.size() + " settings. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
        allowSelfRegister = false;

        Bukkit.getPluginManager().callEvent(new ElevatorRegisterSettingsEvent());
    }

    public static void addSetting(ElevatorSetting<?> setting) {
        if(setting.getPlugin().getName().equalsIgnoreCase(Elevators.getInstance().getName()) && !allowSelfRegister)
            throw new RuntimeException("An invalid Plugin was provided when trying to register an Elevator Setting.");

        for(ElevatorSetting<?> otherSetting : elevatorSettings) {
            if(!otherSetting.getSettingName().equalsIgnoreCase(setting.getSettingName()))
                continue;

            String message;
            if(otherSetting.getPlugin().getName().equalsIgnoreCase(Elevators.getInstance().getName()))
                message = "External elevator settings are not able to override default settings";
            else
                message = "An elevator setting with the key \"" + setting.getSettingName() + "\" was already registered by plugin: " + otherSetting.getPlugin().getName();
            throw new RuntimeException(message);
        }

        elevatorSettings.add(setting);
    }

    public static List<ElevatorSetting<?>> getElevatorSettings() {
        return new ArrayList<>(elevatorSettings);
    }

    public static Optional<ElevatorSetting<?>> getElevatorSetting(String settingsKey) {
        return elevatorSettings.stream().filter(s -> s.getSettingName().equalsIgnoreCase(settingsKey)).findFirst();
    }

    public static <T> T getElevatorSettingValue(Elevator elevator, String settingsKey) {
        Optional<ElevatorSetting<?>> setting = getElevatorSetting(settingsKey);
        if(!setting.isPresent())
            return null;

        try {
            return (T) setting.get().getIndividualValue(elevator);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getElevatorSettingValue(ElevatorType elevatorType, String settingsKey) {
        Optional<ElevatorSetting<?>> setting = getElevatorSetting(settingsKey);
        if(!setting.isPresent())
            return null;

        try {
            return (T) setting.get().getGlobalValue(elevatorType);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getElevatorSettingValue(Elevator elevator, InternalElevatorSettingType settingsKey) {
        return getElevatorSettingValue(elevator, settingsKey.getSettingName());
    }

    public static <T> T getElevatorSettingValue(ElevatorType elevatorType, InternalElevatorSettingType settingsKey) {
        return getElevatorSettingValue(elevatorType, settingsKey.getSettingName());
    }



}
