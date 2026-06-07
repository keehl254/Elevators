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

public class MaxDistanceSetting extends InternalElevatorSetting<Integer> {

    public MaxDistanceSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.MAX_DISTANCE.getSettingName(),"Max Distance", "This controls the number of blocks that the origin elevator will search for a destination elevator.", Material.MINECART, ChatColor.DARK_GREEN);
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
    public void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Integer currentValue) {
        returnMethod.run();
    }

    @Override
    public @NotNull Integer getGlobalValue(@NotNull IElevatorType elevatorType) {
        return elevatorType.getMaxDistanceAllowedBetweenElevators();
    }
}
