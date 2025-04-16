package com.lkeehl.elevators.services.hooks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ProtectionHook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SuperiorSkyblock2Hook extends ProtectionHook {

    private static final String USE_ELEVATOR_FLAG = "elevators_use";
    private static final String EDIT_NAME_ELEVATOR_FLAG = "elevators_edit_name";
    private static final String EDIT_SETTINGS_ELEVATOR_FLAG = "elevators_edit_settings";

    private static IslandPrivilege USE_ELEVATOR, EDIT_NAME_ELEVATOR, EDIT_SETTINGS_ELEVATOR;
    private static boolean registered = false;

    public SuperiorSkyblock2Hook() {
        super("SuperiorSkyblock2");
        register();
    }

    public static void register() {
        if (registered)
            return;

        try {
            USE_ELEVATOR = IslandPrivilege.getByName(USE_ELEVATOR_FLAG);
            EDIT_NAME_ELEVATOR = IslandPrivilege.getByName(EDIT_NAME_ELEVATOR_FLAG);
            EDIT_SETTINGS_ELEVATOR = IslandPrivilege.getByName(EDIT_SETTINGS_ELEVATOR_FLAG);
        } catch(NullPointerException e) {
            IslandPrivilege.register(USE_ELEVATOR_FLAG);
            IslandPrivilege.register(EDIT_NAME_ELEVATOR_FLAG);
            IslandPrivilege.register(EDIT_SETTINGS_ELEVATOR_FLAG);
            try {
                USE_ELEVATOR = IslandPrivilege.getByName(USE_ELEVATOR_FLAG);
                EDIT_NAME_ELEVATOR = IslandPrivilege.getByName(EDIT_NAME_ELEVATOR_FLAG);
                EDIT_SETTINGS_ELEVATOR = IslandPrivilege.getByName(EDIT_SETTINGS_ELEVATOR_FLAG);
            } catch(Exception ex) {
                Elevators.getElevatorsLogger().severe("Failed to register SuperiorSkyblock Hook - please open a issue on Github");
                e.printStackTrace();
                return;
            }
        }
        registered = true;
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        if(!registered) return true;
        Island island = SuperiorSkyblockAPI.getIslandAt(elevator.getLocation());
        if (island != null) {
            return island.hasPermission(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()), USE_ELEVATOR);
        }
        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        if(!registered) return null;
        Island island = SuperiorSkyblockAPI.getIslandAt(elevator.getLocation());
        if(island == null)
            return null;

        boolean flagEnabled = this.shouldAllowGuestUse(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether non-members");
        lore.add(ChatColor.GRAY + "can use this elevator.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "SuperiorSkyblock2", Material.DIAMOND, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleAllowMemberUse(elevator);
        onReturn.run();
    }

    @Override
    public boolean canEditName(Player player, Elevator elevator, boolean sendMessage) {
        if(!registered) return true;
        Island island = SuperiorSkyblockAPI.getIslandAt(elevator.getLocation());
        if (island != null) {
            return island.hasPermission(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()), EDIT_NAME_ELEVATOR);
        }
        return true;
    }

    @Override
    public boolean canEditSettings(Player player, Elevator elevator, boolean sendMessage) {
        if(!registered) return true;
        Island island = SuperiorSkyblockAPI.getIslandAt(elevator.getLocation());
        if (island != null) {
            return island.hasPermission(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()), EDIT_SETTINGS_ELEVATOR);
        }
        return false;
    }
}
