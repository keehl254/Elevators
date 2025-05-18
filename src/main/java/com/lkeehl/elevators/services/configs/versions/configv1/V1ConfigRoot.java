package com.lkeehl.elevators.services.configs.versions.configv1;

import com.lkeehl.elevators.util.config.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class V1ConfigRoot implements Config {

    public List<String> recipe = Arrays.asList("www","wew","www");

    public Map<String, String> materials = Map.of("w","wool","e","ender_pearl");

    public int recipeAmount = 1;

    public boolean itemCountUp = true;

    public  int maxStackSize = 16;

    public int maxBlockDistance = 20;

    public boolean colorCheck = true;

    public boolean canExplode = false;

    public boolean stopObstruction = true;

    public boolean colouredWoolCrafting = true;

    public int maxSolidBlocks = 0;

    public boolean playSound = true;

    public float pitch = 1;

    public float volume = 1;

    public String sound = "ENTITY_BLAZE_SHOOT";

    public boolean usePerms = false;

    public String cantUseMessage = "You do not have permission to use this elevator!";

    public String cantCreateMessage = "You do not have permission to create an elevator!";

    public String cantReloadMessage = "You do not have permission to reload elevators!";

    public String cantGiveMessage = "You do not have permission to give elevators!";

    public String notEnoughRoomGive = "You do not have enough space in your inventory! The elevator is on the ground in front of you!";

    public String givenElevatorMessage = "You have been given an elevator!";

    public boolean worldSounds = true;



}
