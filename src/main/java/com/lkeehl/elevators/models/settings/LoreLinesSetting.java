package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.DataContainerService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LoreLinesSetting extends ElevatorSetting<List<String>> {

    public LoreLinesSetting() {
        super("change-lore","Lore Lines", "Click to alter the lore lines that appear on dropped elevators of this type.", Material.LAPIS_LAZULI,ChatColor.DARK_PURPLE);
        this.setGetValueGlobal(ElevatorType::getLore);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, List<String> currentValue) {
        // TODO: Open menu for editing lore
        returnMethod.run();
    }
}
