package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MaxDistanceSetting extends ElevatorSetting<Integer> {

    public MaxDistanceSetting() {
        super("change-max-distance","Max Distance", "This controls the number of blocks that the origin elevator will search for a destination elevator.", Material.MINECART, ChatColor.DARK_GREEN);
        this.setGetValueGlobal(ElevatorType::getMaxDistanceAllowedBetweenElevators);
        this.addAction("Left Click", "Increase Quantity");
        this.addAction("Right Click", "Decrease Quantity");
        this.addAction("Shift Click", "Reset Quantity");
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue) {

        if(clickEvent.isShiftClick()) {
            elevatorType.setMaxDistanceAllowedBetweenElevators(20);
            returnMethod.run();
            return;
        }

        int newValue = currentValue + (clickEvent.isLeftClick() ? 1 : -1);
        newValue = Math.min(Math.max(newValue, -1), 500);
        elevatorType.setMaxDistanceAllowedBetweenElevators(newValue);
        returnMethod.run();

    }
}
