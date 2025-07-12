package me.keehl.elevators.services.configs.versions.configv5;

import me.keehl.elevators.util.ExecutionMode;
import me.keehl.elevators.util.config.Config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class V5ConfigRoot implements Config {

    public String version = "5.0.0";
    public boolean updateCheckerEnabled = true;
    public ExecutionMode effectDestination = ExecutionMode.ORIGIN;
    public ExecutionMode permissionMode = ExecutionMode.BOTH;
    public boolean forceFacingUpwards = true;
    public Map<String, V5ConfigHookData> protectionHooks;
    public V5ConfigLocale locale;
    public boolean allowElevatorDispense = false;
    public List<String> disabledWorlds = Collections.singletonList("example_world");
    public Map<String, V5ConfigEffect> effects;
    public Map<String, V5ConfigElevatorType> elevators;


}