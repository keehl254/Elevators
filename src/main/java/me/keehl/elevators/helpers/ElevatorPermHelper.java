package me.keehl.elevators.helpers;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorRecipeGroup;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.ExecutionMode;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ElevatorPermHelper {

    public static <T extends Recipe & Keyed> boolean canCraftElevatorType(IElevatorType elevatorType, Player player, T recipe) {

        if (!(boolean) Elevators.getSettingService().getElevatorSettingValue(elevatorType, InternalElevatorSettingType.CHECK_PERMS))
            return true;

        Optional<IElevatorRecipeGroup> optRecipeGroup = elevatorType.getRecipeGroups().stream().filter(i -> i.getNameSpacedKeys().contains(recipe.getKey())).findAny();
        if (optRecipeGroup.isEmpty())
            return false;

        IElevatorRecipeGroup recipeGroup = optRecipeGroup.get();
        return recipeGroup.doesPermissibleHavePermissionForRecipe(player, recipe);
    }

    public static boolean canDyeElevatorType(IElevatorType elevatorType, Player player, DyeColor color) {

        if (!(boolean)Elevators.getSettingService().getElevatorSettingValue(elevatorType, InternalElevatorSettingType.CHECK_PERMS))
            return true;

        if (!(boolean)Elevators.getSettingService().getElevatorSettingValue(elevatorType, InternalElevatorSettingType.SUPPORT_DYING))
            return player.hasPermission(elevatorType.getDyePermission());

        if (player.hasPermission(elevatorType.getDyePermission() + ".*"))
            return true;

        return player.hasPermission(elevatorType.getDyePermission() + "." + color.toString());
    }

    public static boolean canUseElevator(Player player, IElevatorEventData elevatorEventData) {

        AtomicBoolean hasPermission = new AtomicBoolean(true);

        // Never set hasPermission to true in this, as we need to account for this checkPermission consumable running with BOTH execution mode.
        Consumer<IElevator> checkPermission = elevator -> {
            if (!hasPermission.get())
                return;

            if (!Elevators.getHooksService().canUseElevator(player, elevator, false)) {
                hasPermission.set(false);
                return;
            }

            boolean shouldCheckSettings = Elevators.getSettingService().getElevatorSettingValue(elevator, InternalElevatorSettingType.CHECK_PERMS);
            if (!shouldCheckSettings || player.hasPermission(elevator.getElevatorType().getUsePermission() + ".*"))
                return;

            if(!player.hasPermission(elevator.getElevatorType().getUsePermission() + "." + elevator.getDyeColor()))
                hasPermission.set(false);
        };

        ExecutionMode.executeConsumerWithMode(Elevators.getConfigService().getRootConfig().getPermissionMode(), elevatorEventData::getElevatorFromExecutionMode, checkPermission);

        return hasPermission.get();
    }


}
