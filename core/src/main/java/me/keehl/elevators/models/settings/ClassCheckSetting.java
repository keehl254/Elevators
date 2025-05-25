package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorDataContainerService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClassCheckSetting extends ElevatorSetting<Boolean> {

    public ClassCheckSetting() {
        super("check-type","Type Check", "If enabled, the destination elevator must be of the same elevator type.", Material.SHULKER_SHELL, ChatColor.LIGHT_PURPLE);
        this.setGetValueGlobal(ElevatorType::checkDestinationElevatorType);
        this.setupDataStore("class-check", ElevatorDataContainerService.booleanPersistentDataType);
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        this.setIndividualElevatorValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setCheckDestinationElevatorType(!currentValue);
        returnMethod.run();
    }
}
