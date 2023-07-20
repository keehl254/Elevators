package com.lkeehl.elevators.services.configs;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ConfigSerializable()
public class ConfigRoot {

    @Comment("Don't Mess With. Deals with config conversion")
    public String version = "5.0.0";

    @Comment("This option controls whether the plugin will check for plugin updates upon startup or every four hours.\nPlayers with the update permission will receive a message if one is available.")
    public boolean updateCheckerEnabled = true;

    @Comment("If playEffectAtDestination is true, any effects applied to an elevator type will instead play at the elevator being teleported to.")
    public boolean playEffectAtDestination = false;

    @Comment("This option controls whether elevators should always face upwards.")
    public boolean forceFacingUpwards = true;

    @Comment("Locale change. All messages support color codes.")
    public ConfigLocale locale;

    @Comment("If this option is enabled, elevators will only work with trusted users in claims by default. Elevators can be changed individually to allow visitors by trusted members.")
    public boolean claimProtectionDefault = true;

    @Comment("Elevators cannot be used in the world names listed below.")
    public List<String> disabledWorlds = Collections.singletonList("example_world");







}
