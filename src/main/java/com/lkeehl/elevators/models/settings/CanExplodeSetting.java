package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

public class CanExplodeSetting extends ElevatorSetting<Boolean> {

    public CanExplodeSetting() {
        super("can-explode","Break With Explosions", "If enabled, the elevator will be able to be broken by explosions.", Material.TNT, ChatColor.RED);
        this.setGetValueGlobal(ElevatorType::canElevatorExplode);
        this.setupDataStore("can-explode", PersistentDataType.BOOLEAN);
    }

    @Override
    public boolean canBeEditedIndividually(Elevator elevator) {
        return super.canBeEditedIndividually(elevator) && !elevator.getElevatorType().canElevatorExplode();
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
