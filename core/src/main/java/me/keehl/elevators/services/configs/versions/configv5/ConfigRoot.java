package me.keehl.elevators.services.configs.versions.configv5;

import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.util.ExecutionMode;
import me.keehl.elevators.util.config.Comments;
import me.keehl.elevators.util.config.Config;

import java.util.*;

public class ConfigRoot implements Config {


    @Comments("Don't Mess With. Deals with config conversion")
    public String version = "5.0.0";

    @Comments({"This option controls whether the plugin will check for plugin updates upon startup or every four hours.", "Players with the update permission will receive a message if one is available."})
    public boolean updateCheckerEnabled = true;

    @Comments({"effectDestination controls which elevator the effects and elevators sounds will play at. Options are:",
    "Origin","Destination","Both"})
    public ExecutionMode effectDestination = ExecutionMode.ORIGIN;

    @Comments({"Dictates which elevator the player needs permission from to teleport. Options are:",
            "Origin","Destination","Both"})
    public ExecutionMode permissionMode = ExecutionMode.BOTH;

    @Comments("This option controls whether elevators should always face upwards.")
    public boolean forceFacingUpwards = true;

    @Comments({"This configuration section will automatically populate as supported protection plugins are detected.",
            "\"allowCustomization\" will determine whether a player can toggle the hook check on and off individually for an elevator.",
    "\"blockNonMemberUseDefault\" sets the default state for the individual elevator's protection. If marked as true, only trusted / members or those with bypass perms will be able to use the elevator."})
    public Map<String, ConfigHookData> protectionHooks;

    @Comments("Locale change. All messages support color codes.")
    public ConfigLocale locale;

    @Comments("If this option is enabled, elevators will be able to be placed using dispensers. Keep in mind that this can be used to bypass the elevator placement permission.")
    public boolean allowElevatorDispense = false;

    @Comments("Elevators cannot be used in the world names listed below.")
    public List<String> disabledWorlds = Collections.singletonList("example_world");

    public Map<String, ConfigEffect> effects;

    public Map<String, ElevatorType> elevators;


}