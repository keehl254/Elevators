package me.keehl.elevators.api.services.configs.versions;

import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.ExecutionMode;
import me.keehl.elevators.api.util.config.Config;

import java.util.List;
import java.util.Map;

public interface IConfigRoot extends Config {

    String getVersion();

    boolean isUpdateCheckerEnabled();

    boolean isHologramServiceEnabled();

    ExecutionMode getEffectDestination();

    ExecutionMode getPermissionMode();

    boolean shouldForceFacingUpwards();

    Map<String, IConfigHookData> getProtectionHooks();

    IConfigLocale getLocale();

    boolean shouldAllowElevatorDispense();

    List<String> getDisabledWorlds();

    Map<String, IConfigEffect> getEffects();

    Map<String, IElevatorType> getElevators();

}