package me.keehl.elevators.services.configs.versions.configv5_2_0;

import me.keehl.elevators.util.config.Comments;
import me.keehl.elevators.util.config.ExpandableConfig;

import java.util.ArrayList;
import java.util.List;

public class ConfigSettings extends ExpandableConfig {

    @Comments("The permission required to use an elevator of this type")
    public String usePermission = "elevators.use.default";

    @Comments("The permission required to dye an elevator of this type.")
    public String dyePermission = "elevators.dye.default";

    @Comments("The item display name of the elevator.")
    public String displayName = "Elevator";

    @Comments({"Allows the addition of a lore to the item stack.","This can be useful both to look great or to create support for plugins with lore blacklists."})
    public List<String> loreLines = new ArrayList<>();

    @Comments("The maximum distance an elevator will search for the receiving end. Set to -1 to disable.")
    public int maxDistance = 20;

    @Comments("The maximum amount of solid blocks that can be between the sending and receiving elevator.")
    public int maxSolidBlocks = -1;

    @Comments("The maximum stack size of elevators.")
    public int maxStackSize = 16;

    @Comments("This option will require both a destination and origin elevator to be of the same type.")
    public boolean classCheck = true;

    @Comments("This option will stop the use of elevators if the receiving elevator has a blocked path.")
    public boolean stopObstruction = true;

    @Comments("If this option is disabled, players will not be able to dye elevators different colors.")
    public boolean supportDying = true;

    @Comments("Whether the elevator will allow teleportation to an elevator of a different color.")
    public boolean checkColor = true;

    @Comments("If enabled, this will require the player to have the 'use' permission for the elevator.")
    public boolean checkPerms = true;

    @Comments("This config option controls whether elevators should be able to explode from TNT or mobs.")
    public boolean canExplode = false;

    @Comments("Allows the addition of a hologram that appears above elevators of this type.")
    public List<String> hologramLines = new ArrayList<>();

}
