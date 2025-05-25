package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorDataContainerService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CheckColorSetting extends ElevatorSetting<Boolean> {

    public CheckColorSetting() {
        super("check-color","Color Check", "If enabled, any destination elevators must be the same color as the origin.", Material.BLUE_DYE, ChatColor.BLUE);
        this.setGetValueGlobal(ElevatorType::shouldValidateSameColor);
        this.setupDataStore("check-color", ElevatorDataContainerService.booleanPersistentDataType);
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        this.setIndividualElevatorValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setShouldValidateColor(!currentValue);
        returnMethod.run();
    }
}
