package com.lkeehl.elevators.services.configs.versions.configv2;

import com.lkeehl.elevators.util.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V2ConfigRoot implements Config {

    public String version = "2.0.0";

    public String cantCreateMessage = "&b&lELEVATORS&f You do not have permission to create an Elevator!";

    public String cantUseMessage = "&b&lELEVATORS&f You do not have permission to use an Elevator!";

    public String cantGiveMessage = "&b&lELEVATORS&f You do not have permission to give Elevators!";

    public String cantReloadMessage = "&b&lELEVATORS&f You do not have permission to reload Elevators!";

    public String notEnoughRoomGiveMessage = "&b&lELEVATORS&f You have been given an Elevator!";

    public String givenElevatorMessage = "&b&lELEVATORS&f You do not have enough space in your inventory! The Elevator is on the ground in front of you!";

    public boolean worldSounds = true;

    public boolean stopObstruction = true;

    public int maxStackSize = 16;

    public float volume = 1.0F;

    public float pitch = 10F;

    public boolean forceFacingUpwards = true;

    public String displayName = "Elevator";

    public int maxDistance = 20;

    public int maxSolidBlocks = -1;

    public boolean coloredOutput = true;

    public boolean checkColor = true;

    public boolean checkPerms = true;

    public boolean canExplode = false;

    public boolean playSound = true;

    public String sound = "ENTITY_BLAZE_SHOOT";

    public String defaultColor = "WHITE";

    public List<String> lore = new ArrayList<>();

    public V2ConfigCommands commands = new V2ConfigCommands();

    public Map<String, V2ConfigRecipe> recipes = new HashMap<>();

}
