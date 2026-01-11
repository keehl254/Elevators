package me.keehl.elevators.settings;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SupportDyingSetting extends InternalElevatorSetting<Boolean> {

    public SupportDyingSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.SUPPORT_DYING.getSettingName(),"Support Elevator Dying", "If enabled, the elevator is able to be dyed via crafting an elevator and a dye", Material.LIGHT_BLUE_TERRACOTTA, ChatColor.LIGHT_PURPLE);
        this.addAction("Left Click", "Toggle Value");
    }

    @Override
    public boolean canBeEditedIndividually(IElevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(Player player, IElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setCanDye(!currentValue);
        returnMethod.run();
    }

    @Override
    public void onClickIndividual(Player player, IElevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        returnMethod.run();
    }

    @Override
    public Boolean getGlobalValue(IElevatorType elevatorType) {
        return elevatorType.canElevatorBeDyed();
    }
}
