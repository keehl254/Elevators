package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.HookService;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

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

    public static boolean canUseElevator(Player player, Elevator elevator, byte direction) {

        if(!HookService.canUseElevator(player, elevator, false))
            return false;

        //TODO: Check per-elevator settings.

        if(!elevator.getElevatorType().doesElevatorRequirePermissions())
            return true;

        //TODO: Add logic. Duh.

        return true;
    }



}
