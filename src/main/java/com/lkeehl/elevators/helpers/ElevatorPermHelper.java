package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

public class ElevatorPermHelper {

    public static <T extends Recipe & Keyed> boolean canCraftElevatorType(ElevatorType elevatorType, Player player, T recipe, DyeColor color) {

        if(!elevatorType.doesElevatorRequirePermissions())
            return true;

        // TODO: Add logic. Duh.

        return true;
    }

    public static boolean canDyeElevatorType(ElevatorType elevatorType, Player player, DyeColor color) {

        if(!elevatorType.doesElevatorRequirePermissions())
            return true;

        // TODO: Add logic. Duh.

        return true;
    }

    public static boolean canUseElevatorType(ElevatorType elevatorType, Player player, ShulkerBox elevator, byte direction) {

        // TODO: Check hooks.

        if(!elevatorType.doesElevatorRequirePermissions())
            return true;

        //TODO: Add logic. Duh.

        return true;
    }



}
