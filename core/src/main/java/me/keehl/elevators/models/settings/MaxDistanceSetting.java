package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MaxDistanceSetting extends InternalElevatorSetting<Integer> {

    public MaxDistanceSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.MAX_DISTANCE.getSettingName(),"Max Distance", "This controls the number of blocks that the origin elevator will search for a destination elevator.", Material.MINECART, ChatColor.DARK_GREEN);
        this.addAction("Left Click", "Increase Quantity");
        this.addAction("Right Click", "Decrease Quantity");
        this.addAction("Shift Click", "Reset Quantity");
    }

    @Override
    public boolean canBeEditedIndividually(Elevator elevator) {
        return false;
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

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue) {
        returnMethod.run();
    }

    @Override
    public Integer getGlobalValue(ElevatorType elevatorType) {
        return elevatorType.getMaxDistanceAllowedBetweenElevators();
    }
}
