package me.keehl.elevators.settings;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.IElevatorDataContainerService;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CheckColorSetting extends InternalElevatorSetting<Boolean> {

    public CheckColorSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.CHECK_COLOR.getSettingName(),"Color Check", "If enabled, any destination elevators must be the same color as the origin.", Material.BLUE_DYE, ChatColor.BLUE);
        this.setupDataStore(this.getSettingName(), IElevatorDataContainerService.booleanPersistentDataType);
        this.addAction("Left Click", "Toggle Value");
    }

    @Override
    public void onClickIndividual(Player player, IElevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        this.setIndividualValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public Boolean getGlobalValue(IElevatorType elevatorType) {
        return elevatorType.shouldValidateSameColor();
    }

    @Override
    public boolean canBeEditedIndividually(IElevator elevator) {
        return true;
    }

    @Override
    public void onClickGlobal(Player player, IElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setShouldValidateColor(!currentValue);
        returnMethod.run();
    }
}
