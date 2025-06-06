package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SupportDyingSetting extends ElevatorSetting<Boolean> {

    public SupportDyingSetting() {
        super("change-support-dying","Support Elevator Dying", "If enabled, the elevator is able to be dyed via crafting an elevator and a dye", Material.LIGHT_BLUE_TERRACOTTA, ChatColor.LIGHT_PURPLE);
        this.setGetValueGlobal(ElevatorType::canElevatorBeDyed);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setCanDye(!currentValue);
        returnMethod.run();
    }
}
