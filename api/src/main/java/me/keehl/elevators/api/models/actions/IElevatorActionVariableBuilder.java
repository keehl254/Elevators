package me.keehl.elevators.api.models.actions;

import me.keehl.elevators.api.models.settings.IElevatorSettingClickContext;
import org.bukkit.Material;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IElevatorActionVariableBuilder<T> {

    IElevatorActionVariableBuilder<T> setDefault(T defaultValue);

    IElevatorActionVariableBuilder<T> setConversion(Function<String, T> conversionFunction);

    IElevatorActionVariableBuilder<T> setAlias(String... alias);

    IElevatorActionVariableBuilder<T> setIconDescription(String description);

    IElevatorActionVariableBuilder<T> setSettingName(String settingName);

    IElevatorActionVariableBuilder<T> setDisplayName(String displayName);

    IElevatorActionVariableBuilder<T> setIconType(Material iconType);

    IElevatorActionVariableBuilder<T> addAction(String action, String description);

    IElevatorActionVariableBuilder<T> onClick(Consumer<IElevatorSettingClickContext<T>> onClick);

    IElevatorActionVariableBuilder<T> allowPerEleCustomization();

}