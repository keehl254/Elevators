package me.keehl.elevators.settings;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DyePermissionSetting extends InternalElevatorSetting<String> {

    public DyePermissionSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.DYE_PERMISSION.getSettingName(),"Dye Permission", "This will change the permission required to dye the elevator.", Material.RED_DYE, ChatColor.GOLD);
        this.addAction("Left Click", "Change DyeColor");
    }

    @Override
    public boolean canBeEditedIndividually(@NotNull IElevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull String currentValue) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.allowReset();

        input.onComplete(result -> {

            if (result == null)
                result = "elevators.dye." + elevatorType.getTypeKey();

            elevatorType.setDyePermission(result);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        Elevators.getLocale().getEnterDyePermissionMessage().send(player);
        input.start();
    }

    @Override
    public void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull String currentValue) {
        returnMethod.run();
    }

    @Override
    public @NotNull String getGlobalValue(@NotNull IElevatorType elevatorType) {
        return elevatorType.getDyePermission();
    }
}
