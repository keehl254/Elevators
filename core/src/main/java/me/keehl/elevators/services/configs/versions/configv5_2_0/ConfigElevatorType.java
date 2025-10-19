package me.keehl.elevators.services.configs.versions.configv5_2_0;

import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.util.config.Comments;
import me.keehl.elevators.util.config.Config;

import java.util.*;

public class ConfigElevatorType implements Config {

    @Comments({"Control default setting values of the elevator type."})
    protected ConfigSettings settings = new ConfigSettings();

    @Comments({"Define actions that can be run on elevator usage.",
            "Default actions are:",
            "action-bar, boss-bar, command-console",
            "command-player, message-all, message-player",
            "sound, title, effect, cost, trigger-observer, charge-exp",
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

    /**
     * @return the elevators ItemStack display name.
     */
    public String getDisplayName() {
        return this.settings.displayName;
    }

    public String getUsePermission() {
        return this.settings.usePermission;
    }

    public String getDyePermission() {
        return this.settings.dyePermission;
    }

    /**
     * @return the max stack size of an elevator ItemStack
     */
    public int getMaxStackSize() {
        return this.settings.maxStackSize;
    }

    /**
     * @return the lore of an elevator ItemStack
     */
    public List<String> getLore() {
        return this.settings.loreLines;
    }

    /**
     * @return the maximum distance that an elevator will search for a destination elevator.
     */
    public int getMaxDistanceAllowedBetweenElevators() {
        return this.settings.maxDistance;
    }

    /**
     * @return the maximum amount of non-air blocks that can be between the origin and destination elevator before
     * it stops searching.
     */
    public int getMaxSolidBlocksAllowedBetweenElevators() {
        return this.settings.maxSolidBlocks;
    }

    /**
     * @return controls whether the elevator must teleport to destination elevator of the same type.
     */
    public boolean checkDestinationElevatorType() {
        return this.settings.classCheck;
    }

    /**
     * @return controls whether the elevator should check permissions to allow operation. This is mostly
     * used for those small, local servers where permissions plugins aren't used.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean doesElevatorRequirePermissions() {
        return this.settings.checkPerms;
    }

    /**
     * @return whether the elevator can explode from creepers, tnt, etc.
     */
    public boolean canElevatorExplode() {
        return this.settings.canExplode;
    }

    /**
     * @return where a recipe for this elevator type can produce a colored elevator.
     */
    public boolean canElevatorBeDyed() {
        return this.settings.supportDying;
    }
    /**
     * @return controls whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    public boolean shouldStopObstructedTeleport() {
        return this.settings.stopObstruction;
    }

    /**
     * @return controls whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    public boolean shouldValidateSameColor() {
        return this.settings.checkColor;
    }

    public boolean shouldAllowIndividualEdit() { return this.settings.canEditIndividually; }

    public List<String> getDisabledSettings() {
        return this.disabledSettings;
    }

    public ConfigActions getActionsConfig() {
        return this.actions;
    }

    public Map<String, ElevatorRecipeGroup> getRecipeMap() {
        return this.recipes;
    }

    public List<String> getHolographicLines() {
        return this.settings.hologramLines;
    }

}
