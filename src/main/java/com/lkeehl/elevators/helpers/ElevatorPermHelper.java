package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorRecipeGroup;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.settings.CheckPermsSetting;
import com.lkeehl.elevators.models.settings.SupportDyingSetting;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.services.ElevatorSettingService;
import com.lkeehl.elevators.services.HookService;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ElevatorPermHelper {

    public static <T extends Recipe & Keyed> boolean canCraftElevatorType(ElevatorType elevatorType, Player player, T recipe) {

        if (!ElevatorSettingService.getSettingValue(elevatorType, CheckPermsSetting.class))
            return true;

        Optional<ElevatorRecipeGroup> optRecipeGroup = elevatorType.getRecipeGroups().stream().filter(i -> i.getNameSpacedKeys().contains(recipe.getKey())).findAny();
        if (optRecipeGroup.isEmpty())
            return false;

        ElevatorRecipeGroup recipeGroup = optRecipeGroup.get();
        return recipeGroup.doesPermissibleHavePermissionForRecipe(player, recipe);
    }

    public static boolean canDyeElevatorType(ElevatorType elevatorType, Player player, DyeColor color) {

        if (!ElevatorSettingService.getSettingValue(elevatorType, CheckPermsSetting.class))
            return true;

        if (!ElevatorSettingService.getSettingValue(elevatorType, SupportDyingSetting.class))
            return player.hasPermission(elevatorType.getDyePermission());

        if (player.hasPermission(elevatorType.getDyePermission() + ".*"))
            return true;

        return player.hasPermission(elevatorType.getDyePermission() + "." + color.toString());
    }

    public static boolean canUseElevator(Player player, ElevatorEventData elevatorEventData) {

        AtomicBoolean hasPermission = new AtomicBoolean(true);

        Consumer<Elevator> checkPermission = elevator -> {
            if (!hasPermission.get())
                return;

            if (!HookService.canUseElevator(player, elevator, false)) {
                hasPermission.set(false);
                return;
            }

            //TODO: Check per-elevator settings.

            if (!ElevatorSettingService.getSettingValue(elevator, CheckPermsSetting.class)) {
                hasPermission.set(true);
                return;
            }

            if (player.hasPermission(elevator.getElevatorType().getUsePermission() + ".*")) {
                hasPermission.set(true);
                return;
            }

            hasPermission.set(player.hasPermission(elevator.getElevatorType().getUsePermission() + "." + elevator.getDyeColor()));
        };

        ExecutionMode.executeConsumerWithMode(ConfigService.getRootConfig().permissionMode, i -> i == ExecutionMode.DESTINATION ? elevatorEventData.getDestination() : elevatorEventData.getOrigin(), checkPermission);

        return hasPermission.get();
    }


}
