package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CheckPermsSetting extends ElevatorSetting<Boolean> {

    public CheckPermsSetting() {
        super("check-perms","Check Perms", "If enabled, the player must have access to elevator 'use', 'dye', and 'craft' permissions to have access to their respective abilities.", Material.ANVIL, ChatColor.DARK_GRAY);
        this.setGetValueGlobal(ElevatorType::doesElevatorRequirePermissions);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Boolean currentValue) {
        elevatorType.setElevatorRequiresPermissions(!currentValue);
        returnMethod.run();
    }

}
