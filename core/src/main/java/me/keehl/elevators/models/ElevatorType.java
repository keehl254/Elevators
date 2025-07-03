package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.services.ElevatorActionService;
import me.keehl.elevators.services.ElevatorHologramService;
import me.keehl.elevators.services.configs.versions.configv5_1_0.ConfigElevatorType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.*;
import java.util.stream.Collectors;

import static me.keehl.elevators.services.ElevatorHologramService.updateHologramsInChunk;

public class ElevatorType extends ConfigElevatorType {

    //region properties
    private transient String elevatorTypeKey;

    private final transient List<ElevatorAction> actionsUp = new ArrayList<>();
    private final transient List<ElevatorAction> actionsDown = new ArrayList<>();

    //endregion

    /* region property getters */

    /**
     * @return the type name of the elevator.
     */
    public String getTypeKey() {
        return this.elevatorTypeKey.toUpperCase();
    }

    public List<ElevatorAction> getActionsUp() {
        return this.actionsUp;
    }

    public List<ElevatorAction> getActionsDown() {
        return this.actionsDown;
    }

    public List<ElevatorRecipeGroup> getRecipeGroups() { return new ArrayList<>(this.recipes.values()); }

    public ConfigElevatorType getConfig() { return this; }

    //endregion

    /* region property setters */

    /**
     *  Sets the elevators ItemStack display name.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Sets the maximum distance that an elevator will search for a destination elevator.
     */
    public void setMaxDistanceAllowedBetweenElevators(int maxDistance) {
        this.maxDistance = maxDistance;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Sets the max stack size of an elevator ItemStack
     */
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Sets the maximum amount of non-air blocks that can be between the origin and destination elevator before
     * it stops searching.
     */
    public void setMaxSolidBlocksAllowedBetweenElevators(int maxSolidBlocks) {
        this.maxSolidBlocks = maxSolidBlocks;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Sets whether the elevator must teleport to destination elevator of the same type.
     */
    public void setCheckDestinationElevatorType(boolean checkType) {
        this.classCheck = checkType;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether the elevator should check permissions to allow operation. This is mostly
     * used for those small, local servers where permissions plugins aren't used.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public void setElevatorRequiresPermissions(boolean checkPerms) {
        this.checkPerms = checkPerms;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether the elevator can explode from creepers, tnt, etc.
     */
    public void setCanElevatorExplode(boolean canExplode) {
        this.canExplode = canExplode;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether a recipe for this elevator type can produce a colored elevator.
     */
    public void setCanDye(boolean supportDying) {
        this.supportDying = supportDying;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    public void setStopsObstructedTeleportation(boolean stopsObstruction) {
        this.stopObstruction = stopsObstruction;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    public void setShouldValidateColor(boolean checkColor) {
        this.checkColor = checkColor;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set the lines that should appear over an elevator of this type.
     */
    public void setHologramLines(List<String> holoLines) {
        boolean checkCreate = this.hologramLines.isEmpty();
        this.hologramLines = holoLines;

        Elevators.getInstance().saveConfig();

        if(!ElevatorHologramService.canUseHolograms())
            return;

        this.updateAllHolograms(checkCreate);
    }

    public void updateAllHolograms(boolean chunkCheck) {

        // Run this later so that we don't hold up admin or interact menus.
        Elevators.getFoliaLib().getScheduler().runNextTick(task -> ElevatorHologramService.updateHologramsOfElevatorType(this));

        if(!chunkCheck)
            return;

        // Oof, this is a lot of nesting. Hate that, but don't want to extract to a method.
        Elevators.getFoliaLib().getScheduler().runNextTick(task -> {
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    updateHologramsInChunk(chunk);
                }
            }
        });
    }

    /**
     * Set the lines that should appear in an itemstack of this type.
     */
    public void setLore(List<String> loreLines) {
        this.loreLines = loreLines;

        Elevators.getInstance().saveConfig();
    }

    //endregion

    @Override()
    public void onSave() {
        this.actions.up = this.getActionsUp().stream().map(ElevatorAction::serialize).collect(Collectors.toList());
        this.actions.down = this.getActionsDown().stream().map(ElevatorAction::serialize).collect(Collectors.toList());
    }

    @Override()
    public void onLoad() {
        this.getActionsUp().clear();
        this.getActionsDown().clear();

        this.getActionsUp().addAll(this.actions.up.stream().map(i -> ElevatorActionService.createActionFromString(this, i)).filter(Objects::nonNull).collect(Collectors.toList()));
        this.getActionsDown().addAll(this.actions.down.stream().map(i -> ElevatorActionService.createActionFromString(this, i)).filter(Objects::nonNull).collect(Collectors.toList()));

        // Enforce uppercase recipe keys.

        Map<String, ElevatorRecipeGroup> newRecipes = new HashMap<>();
        for(String key : this.recipes.keySet()) {
            ElevatorRecipeGroup recipeGroup = this.recipes.get(key);
            newRecipes.put(key.toUpperCase(), recipeGroup);
            recipeGroup.setKey(key.toUpperCase());
        }

        this.recipes = newRecipes;
    }

    @Override()
    public void setKey(String key) {
        this.elevatorTypeKey = key.toUpperCase();
    }





}
