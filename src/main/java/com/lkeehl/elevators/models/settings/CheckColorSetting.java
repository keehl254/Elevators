package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

public class CheckColorSetting extends ElevatorSetting<Boolean> {

    public CheckColorSetting() {
        super("check-color","Color Check", "If enabled, any destination elevators must be the same color as the origin.", Material.BLUE_DYE, ChatColor.BLUE);
        this.setGetValueGlobal(ElevatorType::shouldValidateSameColor);
        this.setupDataStore("check-color", PersistentDataType.BOOLEAN);
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        this.setIndividualElevatorValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setShouldValidateColor(!currentValue);
        returnMethod.run();
    }
}
