package me.keehl.elevators.api.services;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorSetting;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.InternalElevatorSettingType;

import java.util.List;
import java.util.Optional;

public interface IElevatorSettingService extends IElevatorService {

    void addSetting(IElevatorSetting<?> setting);

    List<IElevatorSetting<?>> getElevatorSettings();

    Optional<IElevatorSetting<?>> getElevatorSetting(String settingsKey);

    <T> T getElevatorSettingValue(IElevator elevator, String settingsKey);

    <T> T getElevatorSettingValue(IElevatorType elevatorType, String settingsKey);

    <T> T getElevatorSettingValue(IElevator elevator, InternalElevatorSettingType settingsKey);

    <T> T getElevatorSettingValue(IElevatorType elevatorType, InternalElevatorSettingType settingsKey);
}
