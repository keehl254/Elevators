package me.keehl.elevators.api.models;

import me.keehl.elevators.api.services.configs.versions.IConfigElevatorType;

import java.util.*;

public interface IElevatorType extends IConfigElevatorType {

    /* region property getters */

    /**
     * @return the type name of the elevator.
     */
    String getTypeKey();

    List<IElevatorAction> getActionsUp();

    List<IElevatorAction> getActionsDown();

    List<IElevatorRecipeGroup> getRecipeGroups();

    List<String> getDisabledSettings();

    List<ILocaleComponent> getHolographicLines();

    boolean shouldAllowIndividualEdit();

    String getUsePermission();

    String getDyePermission();

    int getMaxStackSize();

    boolean canElevatorExplode();

    Map<String, IElevatorRecipeGroup> getRecipeMap();

    boolean canElevatorBeDyed();

    boolean shouldValidateSameColor();

    boolean doesElevatorRequirePermissions();

    boolean checkDestinationElevatorType();

    ILocaleComponent getDisplayName();

    List<ILocaleComponent> getLore();

    int getMaxDistanceAllowedBetweenElevators();

    int getMaxSolidBlocksAllowedBetweenElevators();

    boolean shouldStopObstructedTeleport();



    //endregion

    /* region property setters */

    /**
     *  Sets the elevators ItemStack display name.
     */
    void setDisplayName(ILocaleComponent displayName);

    /**
     *  Sets the elevators use permission.
     */
    void setUsePermission(String usePermission);

    /**
     *  Sets the elevators dye permission.
     */
    void setDyePermission(String dyePermission);


    /**
     * Sets the maximum distance that an elevator will search for a destination elevator.
     */
    void setMaxDistanceAllowedBetweenElevators(int maxDistance);

    /**
     * Sets the max stack size of an elevator ItemStack
     */
    void setMaxStackSize(int maxStackSize);

    /**
     * Sets the maximum amount of non-air blocks that can be between the origin and destination elevator before
     * it stops searching.
     */
    void setMaxSolidBlocksAllowedBetweenElevators(int maxSolidBlocks);

    /**
     * Sets whether the elevator must teleport to destination elevator of the same type.
     */
    void setCheckDestinationElevatorType(boolean checkType);

    /**
     * Set whether the elevator should check permissions to allow operation. This is mostly
     * used for those small, local servers where permissions plugins aren't used.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    void setElevatorRequiresPermissions(boolean checkPerms);

    /**
     * Set whether the elevator can explode from creepers, tnt, etc.
     */
    void setCanElevatorExplode(boolean canExplode);

    /**
     * Set whether a recipe for this elevator type can produce a colored elevator.
     */
    void setCanDye(boolean supportDying);

    /**
     * Set whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    void setStopsObstructedTeleportation(boolean stopsObstruction);

    /**
     * Set whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    void setShouldValidateColor(boolean checkColor);

    /**
     * Set whether users can open the settings-edit menu of an elevator type.
     */
    void setShouldAllowIndividualEdit(boolean allowIndividualEdit);

    /**
     * Set the lines that should appear over an elevator of this type.
     */
    void setHologramLines(List<ILocaleComponent> holoLines);

    void updateAllHolograms(boolean chunkCheck);

    /**
     * Set the lines that should appear in an itemstack of this type.
     */
    void setLore(List<ILocaleComponent> loreLines);
    //endregion

}
