package com.lkeehl.elevators.models;

import com.lkeehl.elevators.services.configs.ConfigElevatorType;
import org.bukkit.DyeColor;

import java.util.ArrayList;
import java.util.List;

public class ElevatorType {

    //region properties
    private final String elevatorTypeKey;

    private final ConfigElevatorType elevatorTypeConfig;

    private ElevatorEffect elevatorUpEffect;
    private ElevatorEffect elevatorDownEffect;

    private final List<ElevatorAction> actionsUp = new ArrayList<>();
    private final List<ElevatorAction> actionsDown = new ArrayList<>();

    //endregion

    public ElevatorType(String elevatorTypeKey, ConfigElevatorType elevatorTypeConfig) {
        this.elevatorTypeKey = elevatorTypeKey;
        this.elevatorTypeConfig = elevatorTypeConfig;
    }

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
        return this.elevatorTypeConfig.displayName;
    }

    /**
     * @return the max stack size of an elevator ItemStack
     */
    public int getMaxStackSize() {
        return this.elevatorTypeConfig.maxStackSize;
    }

    /**
     * @return the lore of an elevator ItemStack
     */
    public List<String> getLore() {
        return this.elevatorTypeConfig.loreLines;
    }

    /**
     * @return the maximum distance that an elevator will search for a destination elevator.
     */
    public int getMaxDistanceAllowedBetweenElevators() {
        return this.elevatorTypeConfig.maxDistance;
    }

    /**
     * @return the maximum amount of non-air blocks that can be between the origin and destination elevator before
     * it stops searching.
     */
    public int getMaxSolidBlocksAllowedBetweenElevators() {
        return this.elevatorTypeConfig.maxSolidBlocks;
    }

    /**
     * @return controls whether the elevator must teleport to destination elevator of the same type.
     */
    public boolean checkDestinationElevatorType() {
        return this.elevatorTypeConfig.classCheck;
    }

    /**
     * @return controls whether the elevator should check permissions to allow operation. This is mostly
     * used for those small, local servers where permissions plugins aren't used.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean doesElevatorRequirePermissions() {
        return this.elevatorTypeConfig.checkPerms;
    }

    /**
     * @return whether the elevator can explode from creepers, tnt, etc.
     */
    public boolean canElevatorExplode() {
        return this.elevatorTypeConfig.canExplode;
    }

    /**
     * @return where a recipe for this elevator type can produce a colored elevator.
     */
    public boolean canRecipesProduceColor() {
        return this.elevatorTypeConfig.coloredOutput;
    }
    /**
     * @return controls whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    public boolean canTeleportToObstructedBlock() {
        return !this.elevatorTypeConfig.stopObstruction;
    }

    /**
     * @return controls whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    public boolean canTeleportToOtherColor() {
        return !this.elevatorTypeConfig.checkColor;
    }

    public DyeColor getDefaultElevatorColor() {
        return DyeColor.valueOf(this.elevatorTypeConfig.defaultColor);
    }

    /**
     * @return the effect that will play upon going upwards in an elevator.
     */
    public ElevatorEffect getElevatorUpEffect() { return this.elevatorUpEffect;
    }

    /**
     * @return the effect that will play upon going downwards in an elevator.
     */
    public ElevatorEffect getElevatorDownEffect() {
        return this.elevatorDownEffect;
    }

    public List<ElevatorAction> getActionsUp() {
        return this.actionsUp;
    }

    public List<ElevatorAction> getActionsDown() {
        return this.actionsDown;
    }

    public ConfigElevatorType getConfig() { return this.elevatorTypeConfig; }

    //endregion

    /* region property setters */

    /**
     *  Sets the elevators ItemStack display name.
     */
    public void setDisplayName(String displayName) {
        this.elevatorTypeConfig.displayName = displayName;
        this.save();
    }

    /**
     * Sets the maximum distance that an elevator will search for a destination elevator.
     */
    public void setMaxDistanceAllowedBetweenElevators(int maxDistance) {
        this.elevatorTypeConfig.maxDistance = maxDistance;
        this.save();
    }

    /**
     * Sets the max stack size of an elevator ItemStack
     */
    public void setMaxStackSize(int maxStackSize) {
        this.elevatorTypeConfig.maxStackSize = maxStackSize;
        this.save();
    }

    /**
     * Sets the maximum amount of non-air blocks that can be between the origin and destination elevator before
     * it stops searching.
     */
    public void setMaxSolidBlocksAllowedBetweenElevators(int maxSolidBlocks) {
        this.elevatorTypeConfig.maxSolidBlocks = maxSolidBlocks;
        this.save();
    }

    /**
     * Sets whether the elevator must teleport to destination elevator of the same type.
     */
    public void setCheckDestinationElevatorType(boolean checkType) {
        this.elevatorTypeConfig.classCheck = checkType;
        this.save();
    }

    /**
     * Set whether the elevator should check permissions to allow operation. This is mostly
     * used for those small, local servers where permissions plugins aren't used.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public void setElevatorRequiresPermissions(boolean checkPerms) {
        this.elevatorTypeConfig.checkPerms = checkPerms;
        this.save();
    }

    /**
     * Set whether the elevator can explode from creepers, tnt, etc.
     */
    public void setCanElevatorExplode(boolean canExplode) {
        this.elevatorTypeConfig.canExplode = canExplode;
        this.save();
    }

    /**
     * Set whether a recipe for this elevator type can produce a colored elevator.
     */
    public void setCanRecipesProduceColor(boolean coloredOutput) {
        this.elevatorTypeConfig.coloredOutput = coloredOutput;
        this.save();
    }

    /**
     * Set whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    public void setBlocksObstruction(boolean blocksObstruction) {
        this.elevatorTypeConfig.stopObstruction = blocksObstruction;
        this.save();
    }

    /**
     * Set whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    public void setCanTeleportToOtherColor(boolean checkColor) {
        this.elevatorTypeConfig.checkColor = checkColor;
        this.save();
    }

    /**
     * Set the effect that will play upon going upwards in an elevator.
     */
    public void setElevatorUpEffect(ElevatorEffect elevatorUpEffect) {
        this.elevatorUpEffect = elevatorUpEffect;
        this.elevatorTypeConfig.effects.up = elevatorUpEffect.getEffectKey();
        this.save();
    }

    /**
     * Set the effect that will play upon going downwards in an elevator.
     */
    public void setElevatorDownEffect(ElevatorEffect elevatorDownEffect) {
        this.elevatorDownEffect = elevatorDownEffect;
        this.elevatorTypeConfig.effects.down = elevatorUpEffect.getEffectKey();
        this.save();
    }

    //endregion

    public void save() {

    }





}
