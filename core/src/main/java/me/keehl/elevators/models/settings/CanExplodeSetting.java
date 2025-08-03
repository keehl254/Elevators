package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorDataContainerService;
import me.keehl.elevators.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CanExplodeSetting extends InternalElevatorSetting<Boolean> {

    public CanExplodeSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.CAN_EXPLODE.getSettingName(), "Break With Explosions", "If enabled, the elevator will be able to be broken by explosions.", Material.TNT, ChatColor.RED);
        this.setupDataStore(this.getSettingName(), ElevatorDataContainerService.booleanPersistentDataType);
        this.addAction("Left Click", "Toggle Value");
    }

    @Override
    public boolean canBeEditedIndividually(Elevator elevator) {
        return !elevator.getElevatorType(false).canElevatorExplode();
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        this.setIndividualValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public Boolean getGlobalValue(ElevatorType elevatorType) {
        return elevatorType.canElevatorExplode();
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setCanElevatorExplode(!currentValue);
        returnMethod.run();
    }
}
