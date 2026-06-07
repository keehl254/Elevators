package me.keehl.elevators.settings;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MaxSolidBlocksSetting extends InternalElevatorSetting<Integer> {

    public MaxSolidBlocksSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.MAX_SOLID_BLOCKS.getSettingName(),"Max Solid Blocks", "This controls the maximum number of solid blocks that can be between an origin and destination elevator.", Material.IRON_BLOCK, ChatColor.RED);
        this.addAction("Left Click", "Increase Quantity");
        this.addAction("Right Click", "Decrease Quantity");
        this.addAction("Shift Click", "Reset Quantity");
    }

    @Override
    public boolean canBeEditedIndividually(@NotNull IElevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Integer currentValue) {

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
    public void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Integer currentValue) {
        returnMethod.run();
    }

    @Override
    public @NotNull Integer getGlobalValue(@NotNull IElevatorType elevatorType) {
        return elevatorType.getMaxSolidBlocksAllowedBetweenElevators();
    }

}
