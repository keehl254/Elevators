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

public class AllowIndividualEditSetting extends InternalElevatorSetting<Boolean> {

    public AllowIndividualEditSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.INDIVIDUAL_EDIT.getSettingName(),"Individual Edit", "If enabled, users may access the individual elevator settings UI by shift-right clicking.", Material.COMMAND_BLOCK, ChatColor.YELLOW);
        this.addAction("Left Click", "Toggle Value");
    }

    @Override
    public boolean canBeEditedIndividually(@NotNull IElevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Boolean currentValue) {
        elevatorType.setShouldAllowIndividualEdit(!currentValue);
        returnMethod.run();
    }

    @Override
    public void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull Boolean currentValue) {
        returnMethod.run();
    }

    @Override
    public @NotNull Boolean getGlobalValue(@NotNull IElevatorType elevatorType) {
        return elevatorType.shouldAllowIndividualEdit();
    }
}
