package me.keehl.elevators.models.settings;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AllowIndividualEditSetting extends InternalElevatorSetting<Boolean> {

    public AllowIndividualEditSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.INDIVIDUAL_EDIT.getSettingName(),"Individual Edit", "If enabled, users may access the individual elevator settings UI by shift-right clicking.", Material.COMMAND_BLOCK, ChatColor.YELLOW);
        this.addAction("Left Click", "Toggle Value");
    }

    @Override
    public boolean canBeEditedIndividually(Elevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setShouldAllowIndividualEdit(!currentValue);
        returnMethod.run();
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        returnMethod.run();
    }

    @Override
    public Boolean getGlobalValue(ElevatorType elevatorType) {
        return elevatorType.shouldAllowIndividualEdit();
    }
}
