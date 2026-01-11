package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.IElevatorSetting;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.IElevatorSettingService;
import me.keehl.elevators.events.ElevatorRegisterSettingsEvent;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.*;

public class ElevatorSettingService extends ElevatorService implements IElevatorSettingService {

    protected Runnable registerDefaultSettingsRunnable;

    private boolean initialized = false;
    private boolean allowSelfRegister = false;

    private final List<IElevatorSetting<?>> elevatorSettings = new ArrayList<>();

    public ElevatorSettingService(IElevators elevators) {
        super(elevators);
    }

    public void onInitialize() {
        if (this.initialized)
            return;
        ElevatorsAPI.pushAndHoldLog();

        this.registerDefaultSettings();

        this.initialized = true;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Setting service enabled. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public void onUninitialize() {

    }

    private void registerDefaultSettings() {

        ElevatorsAPI.pushAndHoldLog();

        this.allowSelfRegister = true;
        if(this.registerDefaultSettingsRunnable != null) {
            this.registerDefaultSettingsRunnable.run();
        }

        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Registered " + this.elevatorSettings.size() + " settings. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
        this.allowSelfRegister = false;

        Bukkit.getPluginManager().callEvent(new ElevatorRegisterSettingsEvent());
    }

    public void addSetting(IElevatorSetting<?> setting) {
        if (setting.getPlugin().getName().equalsIgnoreCase(Elevators.getInstance().getName()) && !this.allowSelfRegister)
            throw new RuntimeException("An invalid Plugin was provided when trying to register an Elevator Setting.");

        for (IElevatorSetting<?> otherSetting : this.elevatorSettings) {
            if (!otherSetting.getSettingName().equalsIgnoreCase(setting.getSettingName()))
                continue;

            String message;
            if (otherSetting.getPlugin().getName().equalsIgnoreCase(Elevators.getInstance().getName()))
                message = "External elevator settings are not able to override default settings";
            else
                message = "An elevator setting with the key \"" + setting.getSettingName() + "\" was already registered by plugin: " + otherSetting.getPlugin().getName();
            throw new RuntimeException(message);
        }

        this.elevatorSettings.add(setting);
    }

    public List<IElevatorSetting<?>> getElevatorSettings() {
        return new ArrayList<>(this.elevatorSettings);
    }

    public Optional<IElevatorSetting<?>> getElevatorSetting(String settingsKey) {
        return this.elevatorSettings.stream().filter(s -> s.getSettingName().equalsIgnoreCase(settingsKey)).findFirst();
    }

    public <T> T getElevatorSettingValue(IElevator elevator, String settingsKey) {
        Optional<IElevatorSetting<?>> setting = getElevatorSetting(settingsKey);
        if (!setting.isPresent())
            return null;

        try {
            return (T) setting.get().getIndividualValue(elevator);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T getElevatorSettingValue(IElevatorType elevatorType, String settingsKey) {
        Optional<IElevatorSetting<?>> setting = getElevatorSetting(settingsKey);
        if (!setting.isPresent())
            return null;

        try {
            return (T) setting.get().getGlobalValue(elevatorType);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T getElevatorSettingValue(IElevator elevator, InternalElevatorSettingType settingsKey) {
        return getElevatorSettingValue(elevator, settingsKey.getSettingName());
    }

    public <T> T getElevatorSettingValue(IElevatorType elevatorType, InternalElevatorSettingType settingsKey) {
        return getElevatorSettingValue(elevatorType, settingsKey.getSettingName());
    }


}
