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

public class UsePermissionSetting extends InternalElevatorSetting<String> {

    public UsePermissionSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.USE_PERMISSION.getSettingName(),"Use Permission", "This will change the permission required to use the elevator.", Material.BEACON, ChatColor.GOLD);
        this.addAction("Left Click", "Edit Permission");
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
                result = "elevators.use." + elevatorType.getTypeKey();

            elevatorType.setUsePermission(result);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        Elevators.getLocale().getEnterUsePermissionMessage().send(player);
        input.start();
    }

    @Override
    public void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull String currentValue) {
        returnMethod.run();
    }

    @Override
    public @NotNull String getGlobalValue(@NotNull IElevatorType elevatorType) {
        return elevatorType.getUsePermission();
    }
}
