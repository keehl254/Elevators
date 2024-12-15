package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class StopObstructionSetting extends ElevatorSetting<Boolean> {

    public StopObstructionSetting() {
        super("Stop Obstructed Teleports", "If enabled, the destination elevator must have enough space to teleport the player safely.", Material.PISTON, ChatColor.DARK_GRAY);
        this.setupDataStore("stop-obstruction", PersistentDataType.BOOLEAN);
        this.setGetValueGlobal(ElevatorType::shouldStopObstructedTeleport);
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, Boolean currentValue) {
        this.setIndividualElevatorValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, Boolean currentValue) {
        elevatorType.setStopsObstructedTeleportation(!currentValue);
        returnMethod.run();
    }

}
