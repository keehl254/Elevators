package me.keehl.elevators.helpers;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorEventData;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.settings.CheckPermsSetting;
import me.keehl.elevators.models.settings.SupportDyingSetting;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.ElevatorSettingService;
import me.keehl.elevators.services.ElevatorHookService;
import me.keehl.elevators.util.ExecutionMode;
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
        if (!optRecipeGroup.isPresent())
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

        // Never set hasPermission to true in this, as we need to account for this checkPermission consumable running with BOTH execution mode.
        Consumer<Elevator> checkPermission = elevator -> {
            if (!hasPermission.get())
                return;

            if (!ElevatorHookService.canUseElevator(player, elevator, false)) {
                hasPermission.set(false);
                return;
            }

            boolean shouldCheckSettings = ElevatorSettingService.getSettingValue(elevator, CheckPermsSetting.class);
            if (!shouldCheckSettings || player.hasPermission(elevator.getElevatorType().getUsePermission() + ".*"))
                return;

            if(!player.hasPermission(elevator.getElevatorType().getUsePermission() + "." + elevator.getDyeColor()))
                hasPermission.set(false);
        };

        ExecutionMode.executeConsumerWithMode(ElevatorConfigService.getRootConfig().permissionMode, elevatorEventData::getElevatorFromExecutionMode, checkPermission);

        return hasPermission.get();
    }


}
