package com.lkeehl.elevators.models;

import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ElevatorType {

    //region properties
    private final String elevatorTypeKey;

    private String displayName;
    private int maxStackSize;
    private final List<String> lore = new ArrayList<>();

    private int maxDistance;
    private int maxSolidBlocks;

    private boolean checkType;
    private boolean checkColor;
    private boolean checkPerms;
    private boolean canExplode;
    private boolean coloredOutput;
    private boolean blocksObstruction;

    private final List<ElevatorAction> actionsUp = new ArrayList<>();
    private final List<ElevatorAction> actionsDown = new ArrayList<>();

    //endregion

    public ElevatorType(String elevatorTypeKey) {
        this.elevatorTypeKey = elevatorTypeKey;
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
        return this.displayName;
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
        return this.lore;
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
        return this.checkType;
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
    public boolean canRecipesProduceColor() {
        return this.coloredOutput;
    }
    /**
     * @return controls whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    public boolean canTeleportToObstructedBlock() {
        return !blocksObstruction;
    }

    /**
     * @return controls whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    public boolean canTeleportToOtherColor() {
        return !checkColor;
    }

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
        this.checkType = checkType;
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
    public void setCanRecipesProduceColor(boolean coloredOutput) {
        this.coloredOutput = coloredOutput;
    }

    /**
     * Set whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    public void setBlocksObstruction(boolean blocksObstruction) {
        this.blocksObstruction = blocksObstruction;
    }

    /**
     * Set whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    public void setCanTeleportToOtherColor(boolean checkColor) {
        this.checkColor = checkColor;
    }

    //endregion

    public List<ElevatorAction> getActionsUp() {
        return this.actionsUp;
    }

    public List<ElevatorAction> getActionsDown() {
        return actionsDown;
    }


    public void onElevatorUse(Player player, ShulkerBox origin, ShulkerBox destination, double additionalY, byte direction) {

        List<ElevatorAction> actions = direction == 1 ? this.getActionsUp() : this.getActionsDown();
        actions.forEach(action -> action.execute(origin, destination, this,  player));

    }





}
