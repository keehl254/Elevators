package me.keehl.elevators.settings;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.IElevatorDataContainerService;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CanExplodeSetting extends InternalElevatorSetting<Boolean> {

    public CanExplodeSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.CAN_EXPLODE.getSettingName(), "Break With Explosions", "If enabled, the elevator will be able to be broken by explosions.", Material.TNT, ChatColor.RED);
        this.setupDataStore(this.getSettingName(), IElevatorDataContainerService.booleanPersistentDataType);
        this.addAction("Left Click", "Toggle Value");
    }

    @Override
    public boolean canBeEditedIndividually(@NotNull IElevator elevator) {
        return !elevator.getSnapshotElevatorType().canElevatorExplode();
    }

    @Override
    public void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Boolean currentValue) {
        this.setIndividualValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public @NotNull Boolean getGlobalValue(@NotNull IElevatorType elevatorType) {
        return elevatorType.canElevatorExplode();
    }

    @Override
    public void onClickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Boolean currentValue) {
        elevatorType.setCanElevatorExplode(!currentValue);
        returnMethod.run();
    }
}
