package com.lkeehl.elevators.services.configs;

import com.lkeehl.elevators.util.ExecutionMode;
import com.lkeehl.elevators.util.config.Comments;
import com.lkeehl.elevators.util.config.Config;

import java.util.*;

public class ConfigRoot implements Config {


    @Comments("Don't Mess With. Deals with config conversion")
    public String version = "5.0.0";

    @Comments({"This option controls whether the plugin will check for plugin updates upon startup or every four hours.", "Players with the update permission will receive a message if one is available."})
    public boolean updateCheckerEnabled = true;

    @Comments({"effectDestination controls which elevator the effects and elevators sounds will play at. Options are:",
    "Origin","Destination","Both"})
    public ExecutionMode effectDestination = ExecutionMode.ORIGIN;

    @Comments({"Dictates which elevator the player needs permission from to teleport.Options are:",
            "Origin","Destination","Both"})
    public ExecutionMode permissionMode = ExecutionMode.BOTH;

    @Comments("This option controls whether elevators should always face upwards.")
    public boolean forceFacingUpwards = true;

    @Comments({"This configuration section will automatically populate as supported protection plugins are detected.",
            "\"allowCustomization\" will determine whether a player can toggle the hook check on and off individually for an elevator.",
    "\"blockNonMemberUseDefault\" sets the default state for the individual elevators protection. If marked as true, only trusted / members or those with bypass perms will be able to use the elevator."})
    public Map<String, ConfigHookData> protectionHooks;

    @Comments("Locale change. All messages support color codes.")
    public ConfigLocale locale;

    @Comments("If this option is enabled, elevators will only work with trusted users in claims by default. Elevators can be changed individually to allow visitors by trusted members.")
    public boolean claimProtectionDefault = true;

    @Comments("Elevators cannot be used in the world names listed below.")
    public List<String> disabledWorlds = Arrays.asList("example_world");

    public Map<String, ConfigEffect> effects;

    public Map<String, ConfigElevatorType> elevators;

}