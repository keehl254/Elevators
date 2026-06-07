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

public class MaxStackSizeSetting extends InternalElevatorSetting<Integer> {

    public MaxStackSizeSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.MAX_STACK_SIZE.getSettingName(),"Max Stack Size", "This controls the maximum stack size of elevator item stacks.", Material.COMPARATOR, ChatColor.YELLOW);
        this.addAction("Left Click", "Increase Quantity");
        this.addAction("Right Click", "Decrease Quantity");
    }

    @Override
    public boolean canBeEditedIndividually(@NotNull IElevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Integer currentValue) {
        int newValue = currentValue + (clickEvent.isLeftClick() ? 1 : -1);
        newValue = Math.min(Math.max(newValue, 1), 64);
        elevatorType.setMaxStackSize(newValue);
        returnMethod.run();
    }

    @Override
    public void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Integer currentValue) {
        returnMethod.run();
    }

    @Override
    public @NotNull Integer getGlobalValue(@NotNull IElevatorType elevatorType) {
        return elevatorType.getMaxStackSize();
    }

}
