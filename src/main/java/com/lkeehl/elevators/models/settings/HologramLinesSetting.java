package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.DataContainerService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HologramLinesSetting extends ElevatorSetting<String[]> {

    public HologramLinesSetting() {
        super("change-holo","Hologram Lines", "Click to alter the hologram lines that appear above the elevator.", Material.PAPER, ChatColor.YELLOW);
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

    @Override
    public ItemStack createIcon(Object value, boolean global) {
        List<String> lore = new ArrayList<>();
        String[] loreLines = (String[]) value;

        ItemMeta templateMeta = this.iconTemplate.getItemMeta();
        if (templateMeta.hasLore())
            lore.addAll(Objects.requireNonNull(templateMeta.getLore()));

        lore.add("");
        if (loreLines.length == 0) {
            lore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + "" + ChatColor.BOLD + "None");
        } else {
            lore.add(ChatColor.GRAY + "Current Value: ");
            for (String line : loreLines)
                lore.add("\t" + ChatColor.GOLD + "" + line);
        }

        ItemStack icon = this.iconTemplate.clone();
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setLore(lore);
        icon.setItemMeta(iconMeta);

        return icon;
    }

}
