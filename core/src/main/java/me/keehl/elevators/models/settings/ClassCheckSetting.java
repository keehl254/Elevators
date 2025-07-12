package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorDataContainerService;
import me.keehl.elevators.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ClassCheckSetting extends InternalElevatorSetting<Boolean> {

    public ClassCheckSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.CLASS_CHECK.getSettingName(),"Type Check", "If enabled, the destination elevator must be of the same elevator type.", Material.SHULKER_SHELL, ChatColor.LIGHT_PURPLE);
        this.setupDataStore("class-check", ElevatorDataContainerService.booleanPersistentDataType);
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        this.setIndividualValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public Boolean getGlobalValue(ElevatorType elevatorType) {
        return elevatorType.checkDestinationElevatorType();
    }

    @Override
    public boolean canBeEditedIndividually(Elevator elevator) {
        return true;
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setCheckDestinationElevatorType(!currentValue);
        returnMethod.run();
    }
}
