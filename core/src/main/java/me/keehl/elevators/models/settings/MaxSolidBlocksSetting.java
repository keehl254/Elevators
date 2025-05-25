package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MaxSolidBlocksSetting extends ElevatorSetting<Integer> {

    public MaxSolidBlocksSetting() {
        super("change-max-solid-blocks","Max Solid Blocks", "This controls the maximum number of solid blocks that can be between an origin and destination elevator.", Material.IRON_BLOCK, ChatColor.RED);
        this.setGetValueGlobal(ElevatorType::getMaxSolidBlocksAllowedBetweenElevators);
        this.addAction("Left Click", "Increase Quantity");
        this.addAction("Right Click", "Decrease Quantity");
        this.addAction("Shift Click", "Reset Quantity");
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue) {

        if(clickEvent.isShiftClick()) {
            elevatorType.setMaxSolidBlocksAllowedBetweenElevators(-1);
            returnMethod.run();
            return;
        }

        int newValue = currentValue + (clickEvent.isLeftClick() ? 1 : -1);
        newValue = Math.min(Math.max(newValue, -1), 500);
        elevatorType.setMaxSolidBlocksAllowedBetweenElevators(newValue);
        returnMethod.run();

    }

}
