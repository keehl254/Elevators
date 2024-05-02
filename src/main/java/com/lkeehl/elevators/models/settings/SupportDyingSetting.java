package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SupportDyingSetting extends ElevatorSetting<Boolean> {

    public SupportDyingSetting() {
        super("Support Elevator Dying", "If enabled, the elevator is able to be dyed via crafting an elevator and a dye", Material.LIGHT_BLUE_TERRACOTTA, ChatColor.LIGHT_PURPLE, false);
        this.setGetValueGlobal(ElevatorType::canElevatorBeDyed);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, Boolean currentValue) {
        elevatorType.setCanDye(!currentValue);
        returnMethod.run();
    }
}
