package me.keehl.elevators.services.configs.versions.configv5;

import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.util.config.Comments;
import me.keehl.elevators.util.config.Config;

import java.util.*;

public class ConfigElevatorType implements Config {

    @Comments("The item display name of the elevator.")
    protected String displayName = "Elevator";

    protected String usePermission = "elevators.use.default";

    protected String dyePermission = "elevators.dye.default";

    @Comments("The maximum distance an elevator will search for the receiving end. Set to -1 to disable.")
    protected int maxDistance = 20;

    @Comments("The maximum amount of solid blocks that can be between the sending and receiving elevator.")
    protected int maxSolidBlocks = -1;

    @Comments("The maximum stack size of elevators.")
    protected int maxStackSize = 16;

    @Comments("This option will require both a destination and origin elevator to be of the same type.")
    protected boolean classCheck = true;

    @Comments("This option will stop the use of elevators if the receiving elevator has a blocked path.")
    protected boolean stopObstruction = true;

    @Comments("If this option is disabled, players will not be able to dye elevators different colors.")
    protected boolean supportDying = true;

    @Comments("Whether the elevator will allow teleportation to an elevator of a different color.")
    protected boolean checkColor = true;

    @Comments("If enabled, this will require the player to have the 'use' permission for the elevator.")
    protected boolean checkPerms = true;

    @Comments("This config option controls whether elevators should be able to explode from TNT or mobs.")
    protected boolean canExplode = false;

    @Comments("Allows the addition of a hologram that appears above elevators of this type.")
    protected List<String> hologramLines = new ArrayList<>();

    @Comments({"Allows the addition of a lore to the item stack.","This can be useful both to look great or to create support for plugins with lore blacklists."})
    protected List<String> loreLines = new ArrayList<>();

    @Comments({"Define actions that can be run on elevator usage.",
            "Default actions are:",
            "action-bar, boss-bar, command-console",
            "command-player, message-all, message-player",
            "sound, title, effect, cost",
            "",
            "If you do not wish to use actions on use, you can either delete the actions section",
            "or set the \"up\" and \"down\" values to an empty array with \"[]\". PlaceholderAPI is supported.",
            "Some default effects are: \"arrow\",\"helix\", \"sparkles\"","",
            "IF AN IDENTIFIER KEY HAS BEEN GENERATED, DO NOT MESS WITH IT OR INDIVIDUAL ELEVATOR DATA MAY BE LOST."})
    protected ConfigActions actions = new ConfigActions();

    @Comments({"Define elevator settings that should not be customizable by users.",
                "Available settings are:",
                "can-explode, check-color, check-perms, check-type, change-holo, stop-obstruction",
                "",
                "Custom actions may have their own settings that can be disabled through use of the action key, a forward-slash, and the setting name.",
                "Available action settings are:",
                "message-player/message, sound/sound, sound/volume, sound/pitch, title/title, title/subtitle, action-bar/message, boss-bar/message, effect/effect"})
    protected List<String> disabledSettings = Arrays.asList("change-holo","sound/sound","action-bar/message","boss-bar/message","message-player/message","title/title","title/subtitle","effect/effect");

    @Comments({"Define recipes to craft an elevator type. If you do not wish to have any recipes, replace the section",
    "with an empty map by settings recipes to \"{}\". Example:",
    "recipes: {}"})
    protected Map<String, ElevatorRecipeGroup> recipes = new HashMap<String, ElevatorRecipeGroup>() {{
       put("classic", new ElevatorRecipeGroup());
    }};

    public static class ConfigActions implements Config {

        public List<String> up = Collections.singletonList("sound: ENTITY_BLAZE_SHOOT pitch=2.0 volume=1.0");

        public List<String> down = Collections.singletonList("sound: ENTITY_BLAZE_SHOOT pitch=2.0 volume=1.0");

    }

}
