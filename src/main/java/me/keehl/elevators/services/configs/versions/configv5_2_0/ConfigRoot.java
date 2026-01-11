package me.keehl.elevators.services.configs.versions.configv5_2_0;

import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.configs.versions.IConfigEffect;
import me.keehl.elevators.api.services.configs.versions.IConfigHookData;
import me.keehl.elevators.api.services.configs.versions.IConfigLocale;
import me.keehl.elevators.api.services.configs.versions.IConfigRoot;
import me.keehl.elevators.api.util.ExecutionMode;
import me.keehl.elevators.util.config.Comments;

import java.util.*;

public class ConfigRoot implements IConfigRoot {


    @Comments("Don't Mess With. Deals with config conversion")
    public String version = "5.2.0";

    @Comments({"This option controls whether the plugin will check for plugin updates upon startup or every four hours.", "Players with the update permission will receive a message if one is available."})
    public boolean updateCheckerEnabled = true;

    @Comments({"If this option is disabled, Elevators will not be able to interact with Hologram hooks."})
    public boolean hologramServiceEnabled = true;

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
    public Map<String, IConfigHookData> protectionHooks;

    @Comments("Locale change. All messages support color codes. Adventure markup supported on PaperMC servers, and any server with a plugin that improperly implements it.")
    public IConfigLocale locale;

    @Comments("If this option is enabled, elevators will be able to be placed using dispensers. Keep in mind that this can be used to bypass the elevator placement permission.")
    public boolean allowElevatorDispense = false;

    @Comments("Elevators cannot be used in the world names listed below.")
    public List<String> disabledWorlds = Collections.singletonList("example_world");

    public Map<String, IConfigEffect> effects;

    public Map<String, IElevatorType> elevators;


    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public boolean isUpdateCheckerEnabled() {
        return this.updateCheckerEnabled;
    }

    @Override
    public boolean isHologramServiceEnabled() {
        return this.hologramServiceEnabled;
    }

    @Override
    public ExecutionMode getEffectDestination() {
        return this.effectDestination;
    }

    @Override
    public ExecutionMode getPermissionMode() {
        return this.permissionMode;
    }

    @Override
    public boolean shouldForceFacingUpwards() {
        return this.forceFacingUpwards;
    }

    @Override
    public Map<String, IConfigHookData> getProtectionHooks() {
        return this.protectionHooks;
    }

    @Override
    public IConfigLocale getLocale() {
        return this.locale;
    }

    @Override
    public boolean shouldAllowElevatorDispense() {
        return this.allowElevatorDispense;
    }

    @Override
    public List<String> getDisabledWorlds() {
        return this.disabledWorlds;
    }

    @Override
    public Map<String, IConfigEffect> getEffects() {
        return this.effects;
    }

    @Override
    public Map<String, IElevatorType> getElevators() {
        return this.elevators;
    }
}