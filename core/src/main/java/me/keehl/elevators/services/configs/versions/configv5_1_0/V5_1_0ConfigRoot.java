package me.keehl.elevators.services.configs.versions.configv5_1_0;

import me.keehl.elevators.util.ExecutionMode;
import me.keehl.elevators.util.config.Config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class V5_1_0ConfigRoot implements Config {

    public String version = "5.1.0";
    public boolean updateCheckerEnabled = true;
    public boolean hologramServiceEnabled = true;
    public ExecutionMode effectDestination = ExecutionMode.ORIGIN;
    public ExecutionMode permissionMode = ExecutionMode.BOTH;
    public boolean forceFacingUpwards = true;
    public Map<String, V5_1_0ConfigHookData> protectionHooks;
    public V5_1_0ConfigLocale locale;
    public boolean allowElevatorDispense = false;
    public List<String> disabledWorlds = Collections.singletonList("example_world");

    public Map<String, V5_1_0ConfigEffect> effects;

    public Map<String, V5_1_0ConfigElevatorType> elevators;


}