package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class LoreLinesSetting extends ElevatorSetting<List<String>> {

    public LoreLinesSetting() {
        super("change-lore","Lore Lines", "Click to alter the lore lines that appear on dropped elevators of this type.", Material.LAPIS_LAZULI,ChatColor.DARK_PURPLE);
        this.setGetValueGlobal(ElevatorType::getLore);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, List<String> currentValue) {
        // TODO: Open menu for editing lore
        returnMethod.run();
    }
}
