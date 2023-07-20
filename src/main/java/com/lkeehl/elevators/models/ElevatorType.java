package com.lkeehl.elevators.models;

import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class ElevatorType {

    //region properties
    private String name;
    private String displayName;

    private int maxDistance;
    private int maxSolidBlocks;
    private int maxStackSize;

    private boolean checkType;
    private boolean checkColor;
    private boolean checkPerms;
    private boolean canExplode;
    private boolean blocksObstruction;

    //endregion

    private List<ElevatorAction> actionsUp;
    private List<ElevatorAction> actionsDown;

    /* region property getters */

    /**
     * @return the type name of the elevator.
     */
    public String getTypeName() {
        return this.name.toUpperCase();
    }

    /**
     * @return the elevators ItemStack display name.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * @return the maximum distance that an elevator will search for a destination elevator.
     */
    public int getMaxDistanceAllowedBetweenElevators() {
        return this.maxDistance;
    }

    /**
     * @return the max stack size of an elevator ItemStack
     */
    public int getMaxStackSize() {
        return this.maxStackSize;
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
