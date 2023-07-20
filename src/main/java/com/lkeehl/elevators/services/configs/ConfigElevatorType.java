package com.lkeehl.elevators.services.configs;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ConfigSerializable()
public class ConfigElevatorType {

    @Comment("The item display name of the elevator.")
    public String displayName = "Elevator";

    @Comment("The maximum distance an elevator will search for the receiving end. Set to -1 to disable.")
    public int maxDistance = 20;

    @Comment("The maximum amount of solid blocks that can be between the sending and receiving elevator.")
    public int maxSolidBlocks = -1;

    @Comment("The maximum stack size of elevators.")
    public int maxStackSize = 16;

    @Comment("The cost to use the elevator. Requires vault.")
    public float costUp = 0.0F;

    @Comment("The cost to use the elevator. Requires vault.")
    public float costDown = 0.0F;

    @Comment("This option will require both a destination and origin elevator to be of the same type.")
    public boolean classCheck = true;

    @Comment("This option will stop the use of elevators if the receiving elevator has a blocked path.")
    public boolean stopObstruction = true;

    @Comment("This option controls whether the shulker will support dye colors besides the default.")
    public boolean coloredOutput = true;

    @Comment("Whether the elevator will allow teleportation to an elevator of a different color.")
    public boolean checkColor = true;

    @Comment("If enabled, this will require the player to have the 'use' permission for the elevator.")
    public boolean checkPerms = true;

    @Comment("This config option controls whether elevators should be able to to explode from TNT or mobs.")
    public boolean canExplode = false;

    @Comment("This option sets the default color of the elevator. This is really only useful if 'coloredOutput' is false.")
    public String defaultColor = "WHITE";

    @Comment("Allows the addition of a hologram that appears above elevators of this type.")
    public List<String> hologramLines = new ArrayList<>();

    @Comment("Allows the addition of a lore to the item stack.\nThis can be useful both to look great or to create support for plugins with lore blacklists.")
    public List<String> loreLines = new ArrayList<>();

    @Comment("Define actions that can be run on elevator usage.\nDefault actions are:\naction-bar\nboss-bar\ncommand-console\ncommand-player\nmessage-all\nmessage-player\nsound\ntitle\n\nIf you do not wish to use actions on use, you can either delete the actions section\nor set the \"up\" and \"down\" values to an empty array with \"[]\" such as shown with the \"down\" value. PlaceholderAPI is supported.")
    public ConfigActions actions = new ConfigActions();

    @ConfigSerializable()
    public static class ConfigActions {

        public List<String> up = Collections.singletonList("sound: ENTITY_BLAZE_SHOOT pitch=2.0 volume=1.0");

        public List<String> down = Collections.singletonList("sound: ENTITY_BLAZE_SHOOT pitch=2.0 volume=1.0");

    }

}
