package com.lkeehl.elevators.services.configs.versions.configv4_0_2;

import com.lkeehl.elevators.util.config.Config;
import com.lkeehl.elevators.util.config.ConfigFieldName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V4_0_2ConfigRoot implements Config {

    public String version = "4.0.2";

    public boolean updateCheckerEnabled = true;


    public String cantCreateMessage = "&b&lELEVATORS&f You do not have permission to create an Elevator!";

    public String cantUseMessage = "&b&lELEVATORS&f You do not have permission to use an Elevator!";

    public String cantGiveMessage = "&b&lELEVATORS&f You do not have permission to give Elevators!";

    public String cantReloadMessage = "&b&lELEVATORS&f You do not have permission to reload Elevators!";

    public String notEnoughRoomGiveMessage = "&b&lELEVATORS&f You do not have enough space in your inventory! The Elevator is on the ground in front of you!";

    public String givenElevatorMessage = "&b&lELEVATORS&f You have been given an Elevator!";

    public String worldDisabledMessage = "&b&lELEVATORS&f Elevators have been disabled in this world!";

    public String elevatorNowProtected = "&b&lELEVATORS&f The elevator can now only be used by trusted players.";

    public String elevatorNowUnprotected = "&b&lELEVATORS&f The elevator can now be used by anyone.";

    public boolean worldSounds = true;

    public boolean forceFacingUpwards = true;

    public boolean supportClaims = true;

    public boolean claimProtectionDefault = false;

    @ConfigFieldName("disabled-worlds")
    public List<String> disabledWorlds;

    public String displayName = "Elevator";

    public int maxDistance = 20;

    public int maxSolidBlocks = -1;

    public int maxStackSize = 16;

    public boolean coloredOutput = true;

    public boolean checkColor = true;

    public boolean stopObstruction = true;

    public boolean checkPerms = true;

    public boolean canExplode = false;

    public boolean playSound = true;

    public float volume = 1.0F;

    public float pitch = 10F;

    public String sound = "ENTITY_BLAZE_SHOOT";

    public String defaultColor = "WHITE";

    public List<String> lore = new ArrayList<>();

    public V4_0_2ConfigActions actions = new V4_0_2ConfigActions();

    public Map<String, V4_0_2ConfigRecipe> recipes = new HashMap<>();



}
