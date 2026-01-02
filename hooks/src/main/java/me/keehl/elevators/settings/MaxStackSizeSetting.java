package me.keehl.elevators.settings;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MaxStackSizeSetting extends InternalElevatorSetting<Integer> {

    public MaxStackSizeSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.MAX_STACK_SIZE.getSettingName(),"Max Stack Size", "This controls the maximum stack size of elevator item stacks.", Material.COMPARATOR, ChatColor.YELLOW);
        this.addAction("Left Click", "Increase Quantity");
        this.addAction("Right Click", "Decrease Quantity");
    }

    @Override
    public boolean canBeEditedIndividually(Elevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue) {
        int newValue = currentValue + (clickEvent.isLeftClick() ? 1 : -1);
        newValue = Math.min(Math.max(newValue, 1), 64);
        elevatorType.setMaxStackSize(newValue);
        returnMethod.run();
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue) {
        returnMethod.run();
    }

    @Override
    public Integer getGlobalValue(ElevatorType elevatorType) {
        return elevatorType.getMaxStackSize();
    }

}
