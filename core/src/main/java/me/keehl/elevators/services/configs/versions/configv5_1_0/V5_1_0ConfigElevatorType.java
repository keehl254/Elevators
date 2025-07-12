package me.keehl.elevators.services.configs.versions.configv5_1_0;

import me.keehl.elevators.util.config.Config;

import java.util.*;

public class V5_1_0ConfigElevatorType implements Config {

    protected String displayName = "Elevator";
    protected String usePermission = "elevators.use.default";
    protected String dyePermission = "elevators.dye.default";
    protected int maxDistance = 20;
    protected int maxSolidBlocks = -1;
    protected int maxStackSize = 16;
    protected boolean classCheck = true;
    protected boolean stopObstruction = true;
    protected boolean supportDying = true;
    protected boolean checkColor = true;
    protected boolean checkPerms = true;
    protected boolean canExplode = false;
    protected List<String> hologramLines = new ArrayList<>();
    protected List<String> loreLines = new ArrayList<>();
    protected ConfigActions actions = new ConfigActions();
    protected List<String> disabledSettings = Arrays.asList("change-holo","sound/sound","action-bar/message","boss-bar/message","message-player/message","title/title","title/subtitle","effect/effect");
    protected Map<String, V5_1_0ConfigRecipe> recipes = new HashMap<String, V5_1_0ConfigRecipe>() {{
       put("classic", new V5_1_0ConfigRecipe());
    }};

    public static class ConfigActions implements Config {

        public List<String> up = Collections.singletonList("sound: ENTITY_BLAZE_SHOOT pitch=2.0 volume=1.0");

        public List<String> down = Collections.singletonList("sound: ENTITY_BLAZE_SHOOT pitch=2.0 volume=1.0");

    }

    /**
     * @return the elevators ItemStack display name.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    public String getUsePermission() {
        return this.usePermission;
    }

    public String getDyePermission() {
        return this.dyePermission;
    }

    /**
     * @return the max stack size of an elevator ItemStack
     */
    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    /**
     * @return the lore of an elevator ItemStack
     */
    public List<String> getLore() {
        return this.loreLines;
    }

    /**
     * @return the maximum distance that an elevator will search for a destination elevator.
     */
    public int getMaxDistanceAllowedBetweenElevators() {
        return this.maxDistance;
    }

    /**
     * @return the maximum amount of non-air blocks that can be between the origin and destination elevator before
     * it stops searching.
     */
    public int getMaxSolidBlocksAllowedBetweenElevators() {
        return this.maxSolidBlocks;
    }

    /**
     * @return controls whether the elevator must teleport to destination elevator of the same type.
     */
    public boolean checkDestinationElevatorType() {
        return this.classCheck;
    }

    /**
     * @return controls whether the elevator should check permissions to allow operation. This is mostly
     * used for those small, local servers where permissions plugins aren't used.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean doesElevatorRequirePermissions() {
        return this.checkPerms;
    }

    /**
     * @return whether the elevator can explode from creepers, tnt, etc.
     */
    public boolean canElevatorExplode() {
        return this.canExplode;
    }

    /**
     * @return where a recipe for this elevator type can produce a colored elevator.
     */
    public boolean canElevatorBeDyed() {
        return this.supportDying;
    }
    /**
     * @return controls whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    public boolean shouldStopObstructedTeleport() {
        return this.stopObstruction;
    }

    /**
     * @return controls whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    public boolean shouldValidateSameColor() {
        return this.checkColor;
    }

    public List<String> getDisabledSettings() {
        return this.disabledSettings;
    }

    public ConfigActions getActionsConfig() {
        return this.actions;
    }

    public Map<String, V5_1_0ConfigRecipe> getRecipeMap() {
        return this.recipes;
    }

    public List<String> getHolographicLines() {
        return this.hologramLines;
    }

}
