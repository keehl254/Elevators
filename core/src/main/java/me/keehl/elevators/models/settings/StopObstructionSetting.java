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

public class StopObstructionSetting extends InternalElevatorSetting<Boolean> {

    public StopObstructionSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.STOP_OBSTRUCTION.getSettingName(),"Stop Obstructed Teleports", "If enabled, the destination elevator must have enough space to teleport the player safely.", Material.PISTON, ChatColor.DARK_GRAY);
        this.setupDataStore(this.getSettingName(), ElevatorDataContainerService.booleanPersistentDataType);
        this.addAction("Left Click", "Toggle Value");
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        this.setIndividualValue(elevator, !currentValue);
        returnMethod.run();
    }

    @Override
    public Boolean getGlobalValue(ElevatorType elevatorType) {
        return elevatorType.shouldStopObstructedTeleport();
    }

    @Override
    public boolean canBeEditedIndividually(Elevator elevator) {
        return true;
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setStopsObstructedTeleportation(!currentValue);
        returnMethod.run();
    }

}
