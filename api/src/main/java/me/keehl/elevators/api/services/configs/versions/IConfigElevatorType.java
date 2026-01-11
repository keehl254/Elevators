package me.keehl.elevators.api.services.configs.versions;

import me.keehl.elevators.api.models.IElevatorRecipeGroup;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.util.config.Config;

import java.util.*;

public interface IConfigElevatorType extends Config {

    interface IConfigActions extends Config {

        List<String> getUpActions();

        List<String> getDownActions();

    }

    /**
     * @return the elevators ItemStack display name.
     */
    ILocaleComponent getDisplayName();

    String getUsePermission();

    String getDyePermission();

    /**
     * @return the max stack size of an elevator ItemStack
     */
    int getMaxStackSize();

    /**
     * @return the lore of an elevator ItemStack
     */
    List<ILocaleComponent> getLore();

    /**
     * @return the maximum distance that an elevator will search for a destination elevator.
     */
    int getMaxDistanceAllowedBetweenElevators();

    /**
     * @return the maximum amount of non-air blocks that can be between the origin and destination elevator before
     * it stops searching.
     */
    int getMaxSolidBlocksAllowedBetweenElevators();

    /**
     * @return controls whether the elevator must teleport to destination elevator of the same type.
     */
    boolean checkDestinationElevatorType();

    /**
     * @return controls whether the elevator should check permissions to allow operation. This is mostly
     * used for those small, local servers where permissions plugins aren't used.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean doesElevatorRequirePermissions();

    /**
     * @return whether the elevator can explode from creepers, tnt, etc.
     */
    boolean canElevatorExplode();

    /**
     * @return where a recipe for this elevator type can produce a colored elevator.
     */
    boolean canElevatorBeDyed();
    /**
     * @return controls whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    boolean shouldStopObstructedTeleport();

    /**
     * @return controls whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    boolean shouldValidateSameColor();

    boolean shouldAllowIndividualEdit();

    List<String> getDisabledSettings();

    IConfigActions getActionsConfig();

    Map<String, IElevatorRecipeGroup> getRecipeMap();

    List<ILocaleComponent> getHolographicLines();

}
