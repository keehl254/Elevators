package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorRecipeGroup;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.services.HookService;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ElevatorPermHelper {

    public static <T extends Recipe & Keyed> boolean canCraftElevatorType(ElevatorType elevatorType, Player player, T recipe) {

        if (!elevatorType.doesElevatorRequirePermissions())
            return true;

        Optional<ElevatorRecipeGroup> optRecipeGroup = elevatorType.getRecipeGroups().stream().filter(i -> i.getNameSpacedKeys().contains(recipe.getKey())).findAny();
        if (optRecipeGroup.isEmpty())
            return false;

        ElevatorRecipeGroup recipeGroup = optRecipeGroup.get();
        return recipeGroup.doesPermissibleHavePermissionForRecipe(player, recipe);
    }

    public static boolean canDyeElevatorType(ElevatorType elevatorType, Player player, DyeColor color) {

        if (!elevatorType.doesElevatorRequirePermissions())
            return true;

        if (!elevatorType.canRecipesProduceColor())
            return player.hasPermission("elevators.dye." + elevatorType.getTypeKey());

        if (player.hasPermission("elevators.dye.*"))
            return true;

        return player.hasPermission("elevators.dye." + color.toString());
    }

    public static boolean canUseElevator(Player player, ElevatorEventData elevatorEventData) {

        AtomicBoolean hasPermission = new AtomicBoolean(true);

        Consumer<ShulkerBox> checkPermission = box -> {
            if (!hasPermission.get())
                return;

            Elevator elevator = new Elevator(box, ElevatorHelper.getElevatorType(box));

            if (!HookService.canUseElevator(player, elevator, false)) {
                hasPermission.set(false);
                return;
            }

            //TODO: Check per-elevator settings.

            if (!elevator.getElevatorType().doesElevatorRequirePermissions()) {
                hasPermission.set(true);
                return;
            }

            DyeColor elevatorColor = elevator.getShulkerBox().getColor(); // Elevators should only support colored shulkerboxes... but just in case
            if (!elevator.getElevatorType().canRecipesProduceColor() || elevatorColor == null) {
                hasPermission.set(player.hasPermission("elevators.use." + elevator.getElevatorType().getTypeKey()));
                return;
            }

            if (player.hasPermission("elevators.use.*")) {
                hasPermission.set(true);
                return;
            }

            hasPermission.set(player.hasPermission("elevators.use." + elevatorColor));
        };

        ExecutionMode.executeConsumerWithMode(ConfigService.getRootConfig().permissionMode, i -> i == ExecutionMode.DESTINATION ? elevatorEventData.getDestination() : elevatorEventData.getOrigin(), checkPermission);

        return hasPermission.get();
    }


}
