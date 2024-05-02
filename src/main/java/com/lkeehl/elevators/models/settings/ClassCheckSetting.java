package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ClassCheckSetting extends ElevatorSetting<Boolean> {

    public ClassCheckSetting() {
        super("Type Check", "If enabled, the destination elevator must be of the same elevator type.", Material.SHULKER_SHELL, ChatColor.LIGHT_PURPLE,true);
        this.setGetValueGlobal(ElevatorType::checkDestinationElevatorType);
        this.setupDataStore("class-check", PersistentDataType.BOOLEAN);
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, Boolean currentValue) {
        this.setIndividualElevatorValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, Boolean currentValue) {
        elevatorType.setCheckDestinationElevatorType(!currentValue);
        returnMethod.run();
    }
}
