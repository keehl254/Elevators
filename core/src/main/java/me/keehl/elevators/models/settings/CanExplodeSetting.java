package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorDataContainerService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CanExplodeSetting extends ElevatorSetting<Boolean> {

    public CanExplodeSetting() {
        super("can-explode","Break With Explosions", "If enabled, the elevator will be able to be broken by explosions.", Material.TNT, ChatColor.RED);
        this.setGetValueGlobal(ElevatorType::canElevatorExplode);
        this.setupDataStore("can-explode", ElevatorDataContainerService.booleanPersistentDataType);
    }

    @Override
    public boolean canBeEditedIndividually(Elevator elevator) {
        return super.canBeEditedIndividually(elevator) && !elevator.getElevatorType(false).canElevatorExplode();
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        this.setIndividualElevatorValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setCanElevatorExplode(!currentValue);
        returnMethod.run();
    }
}
