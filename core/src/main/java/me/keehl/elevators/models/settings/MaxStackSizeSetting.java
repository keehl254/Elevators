package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MaxStackSizeSetting extends ElevatorSetting<Integer> {

    public MaxStackSizeSetting() {
        super("change-max-stack-size","Max Stack Size", "This controls the maximum stack size of elevator item stacks.", Material.COMPARATOR, ChatColor.YELLOW);
        this.setGetValueGlobal(ElevatorType::getMaxStackSize);
        this.addAction("Left Click", "Increase Quantity");
        this.addAction("Right Click", "Decrease Quantity");
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue) {
        int newValue = currentValue + (clickEvent.isLeftClick() ? 1 : -1);
        newValue = Math.min(Math.max(newValue, 1), 64);
        elevatorType.setMaxStackSize(newValue);
        returnMethod.run();
    }

}
