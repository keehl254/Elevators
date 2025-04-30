package com.lkeehl.elevators.models;

import com.lkeehl.elevators.services.ElevatorActionService;
import com.lkeehl.elevators.services.ElevatorRecipeService;
import com.lkeehl.elevators.services.configs.ConfigElevatorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ElevatorType extends ConfigElevatorType {

    //region properties
    private transient String elevatorTypeKey;

    private transient final List<ElevatorAction> actionsUp = new ArrayList<>();
    private transient final List<ElevatorAction> actionsDown = new ArrayList<>();

    //endregion

    /* region property getters */

    /**
     * @return the type name of the elevator.
     */
    public String getTypeKey() {
        return this.elevatorTypeKey.toUpperCase();
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

    public List<ElevatorAction> getActionsUp() {
        return this.actionsUp;
    }

    public List<ElevatorAction> getActionsDown() {
        return this.actionsDown;
    }

    public List<ElevatorRecipeGroup> getRecipeGroups() { return new ArrayList<>(this.recipes.values()); }

    public List<String> getHolographicLines() {
        return this.hologramLines;
    }

    public ConfigElevatorType getConfig() { return this; }

    //endregion

    /* region property setters */

    /**
     *  Sets the elevators ItemStack display name.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Sets the maximum distance that an elevator will search for a destination elevator.
     */
    public void setMaxDistanceAllowedBetweenElevators(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    /**
     * Sets the max stack size of an elevator ItemStack
     */
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    /**
     * Sets the maximum amount of non-air blocks that can be between the origin and destination elevator before
     * it stops searching.
     */
    public void setMaxSolidBlocksAllowedBetweenElevators(int maxSolidBlocks) {
        this.maxSolidBlocks = maxSolidBlocks;
    }

    /**
     * Sets whether the elevator must teleport to destination elevator of the same type.
     */
    public void setCheckDestinationElevatorType(boolean checkType) {
        this.classCheck = checkType;
    }

    /**
     * Set whether the elevator should check permissions to allow operation. This is mostly
     * used for those small, local servers where permissions plugins aren't used.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public void setElevatorRequiresPermissions(boolean checkPerms) {
        this.checkPerms = checkPerms;
    }

    /**
     * Set whether the elevator can explode from creepers, tnt, etc.
     */
    public void setCanElevatorExplode(boolean canExplode) {
        this.canExplode = canExplode;
    }

    /**
     * Set whether a recipe for this elevator type can produce a colored elevator.
     */
    public void setCanDye(boolean supportDying) {
        this.supportDying = supportDying;
    }

    /**
     * Set whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    public void setStopsObstructedTeleportation(boolean stopsObstruction) {
        this.stopObstruction = stopsObstruction;
    }

    /**
     * Set whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    public void setShouldValidateColor(boolean checkColor) {
        this.checkColor = checkColor;
    }

    //endregion

    @Override()
    public void onSave() {
        this.actions.up = this.getActionsUp().stream().map(ElevatorAction::serialize).toList();
        this.actions.down = this.getActionsDown().stream().map(ElevatorAction::serialize).toList();
    }

    @Override()
    public void onLoad() {
        this.getActionsUp().addAll(this.actions.up.stream().map(i -> ElevatorActionService.createActionFromString(this, i)).filter(Objects::nonNull).toList());
        this.getActionsDown().addAll(this.actions.down.stream().map(i -> ElevatorActionService.createActionFromString(this, i)).filter(Objects::nonNull).toList());
    }

    @Override()
    public void setKey(String key) {
        this.elevatorTypeKey = key.toUpperCase();
    }





}
