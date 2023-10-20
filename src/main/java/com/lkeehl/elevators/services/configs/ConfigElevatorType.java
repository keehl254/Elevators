package com.lkeehl.elevators.services.configs;

import com.lkeehl.elevators.util.config.Comments;
import com.lkeehl.elevators.util.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConfigElevatorType implements Config {

    @Comments("The item display name of the elevator.")
    public String displayName = "Elevator";

    @Comments("The maximum distance an elevator will search for the receiving end. Set to -1 to disable.")
    public int maxDistance = 20;

    @Comments("The maximum amount of solid blocks that can be between the sending and receiving elevator.")
    public int maxSolidBlocks = -1;

    @Comments("The maximum stack size of elevators.")
    public int maxStackSize = 16;

    @Comments("The cost to use the elevator. Requires vault.")
    public float costUp = 0.0F;

    @Comments("The cost to use the elevator. Requires vault.")
    public float costDown = 0.0F;

    @Comments("This option will require both a destination and origin elevator to be of the same type.")
    public boolean classCheck = true;

    @Comments("This option will stop the use of elevators if the receiving elevator has a blocked path.")
    public boolean stopObstruction = true;

    @Comments("This option controls whether the shulker will support dye colors besides the default.")
    public boolean coloredOutput = true;

    @Comments("Whether the elevator will allow teleportation to an elevator of a different color.")
    public boolean checkColor = true;

    @Comments("If enabled, this will require the player to have the 'use' permission for the elevator.")
    public boolean checkPerms = true;

    @Comments("This config option controls whether elevators should be able to to explode from TNT or mobs.")
    public boolean canExplode = false;

    @Comments("This option sets the default color of the elevator. This is really only useful if 'coloredOutput' is false.")
    public String defaultColor = "WHITE";

    @Comments("Allows the addition of a hologram that appears above elevators of this type.")
    public List<String> hologramLines = new ArrayList<>();

    @Comments({"Allows the addition of a lore to the item stack.","This can be useful both to look great or to create support for plugins with lore blacklists."})
    public List<String> loreLines = new ArrayList<>();

    @Comments({"Define actions that can be run on elevator usage.","Default actions are:","action-bar","boss-bar","command-console","command-player","message-all","message-player","sound","title","","If you do not wish to use actions on use, you can either delete the actions section","or set the \"up\" and \"down\" values to an empty array with \"[]\" such as shown with the \"down\" value. PlaceholderAPI is supported."})
    public ConfigActions actions = new ConfigActions();

    @Comments("Define a cost to use the elevator. Requires Vault to be installed on the server to work. Set to 0 or below to disable.")
    public ConfigCosts cost = new ConfigCosts();

    @Comments({"Define effects that should play when an elevator is used. Effects can be created from image files using the #Effects section of this config above, or you can you can use predefined animations such as:","arrow","helix","sparkles","none"})
    public ConfigEffects effects = new ConfigEffects();

    public Map<String, ConfigRecipe> recipes;

    public static class ConfigActions implements Config {

        public List<String> up = Collections.singletonList("sound: ENTITY_BLAZE_SHOOT pitch=2.0 volume=1.0");

        public List<String> down = Collections.singletonList("sound: ENTITY_BLAZE_SHOOT pitch=2.0 volume=1.0");

    }

    public static class ConfigCosts implements Config {

        public double up = 0.0D;

        public double down = 0.0D;

    }

    public static class ConfigEffects implements Config {

        public String up = "sparkle";

        public String down = "sparkle";

    }

}
