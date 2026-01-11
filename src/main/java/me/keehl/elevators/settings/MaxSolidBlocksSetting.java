package me.keehl.elevators.settings;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MaxSolidBlocksSetting extends InternalElevatorSetting<Integer> {

    public MaxSolidBlocksSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.MAX_SOLID_BLOCKS.getSettingName(),"Max Solid Blocks", "This controls the maximum number of solid blocks that can be between an origin and destination elevator.", Material.IRON_BLOCK, ChatColor.RED);
        this.addAction("Left Click", "Increase Quantity");
        this.addAction("Right Click", "Decrease Quantity");
        this.addAction("Shift Click", "Reset Quantity");
    }

    @Override
    public boolean canBeEditedIndividually(IElevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(Player player, IElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue) {

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

    @Override
    public void onClickIndividual(Player player, IElevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue) {
        returnMethod.run();
    }

    @Override
    public Integer getGlobalValue(IElevatorType elevatorType) {
        return elevatorType.getMaxSolidBlocksAllowedBetweenElevators();
    }

}
