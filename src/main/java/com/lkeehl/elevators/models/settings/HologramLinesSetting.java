package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.DataContainerService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HologramLinesSetting extends ElevatorSetting<String[]> {

    public HologramLinesSetting() {
        super("Hologram Lines", "Click to alter the hologram lines that appear above the elevator.", Material.PAPER, ChatColor.YELLOW);
        this.setGetValueGlobal(e -> e.getHolographicLines().toArray(new String[]{}));
        this.setupDataStore("hologram-lines", DataContainerService.stringArrayPersistentDataType);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, String[] currentValue) {
        // TODO: Open menu for editing holograms
        returnMethod.run();
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, String[] currentValue) {
        // TODO: Open menu for editing holograms
        returnMethod.run();
    }
}
