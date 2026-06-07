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

public class SupportDyingSetting extends InternalElevatorSetting<Boolean> {

    public SupportDyingSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.SUPPORT_DYING.getSettingName(),"Support Elevator Dying", "If enabled, the elevator is able to be dyed via crafting an elevator and a dye", Material.LIGHT_BLUE_TERRACOTTA, ChatColor.LIGHT_PURPLE);
        this.addAction("Left Click", "Toggle Value");
    }

    @Override
    public boolean canBeEditedIndividually(@NotNull IElevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Boolean currentValue) {
        elevatorType.setCanDye(!currentValue);
        returnMethod.run();
    }

    @Override
    public void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Boolean currentValue) {
        returnMethod.run();
    }

    @Override
    public @NotNull Boolean getGlobalValue(@NotNull IElevatorType elevatorType) {
        return elevatorType.canElevatorBeDyed();
    }
}
