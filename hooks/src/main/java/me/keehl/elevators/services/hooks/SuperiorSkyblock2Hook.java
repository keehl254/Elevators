package me.keehl.elevators.services.hooks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.PluginInitializeEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.ProtectionHook;
import me.keehl.elevators.services.ElevatorListenerService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public class SuperiorSkyblock2Hook extends ProtectionHook {

    private static final String USE_ELEVATOR_FLAG = "elevators_use";
    private static final String EDIT_NAME_ELEVATOR_FLAG = "elevators_edit_name";
    private static final String EDIT_SETTINGS_ELEVATOR_FLAG = "elevators_edit_settings";

    private static IslandPrivilege USE_ELEVATOR, EDIT_NAME_ELEVATOR, EDIT_SETTINGS_ELEVATOR;
    private static boolean registered = false;

    public SuperiorSkyblock2Hook() {
        super("SuperiorSkyblock2");
        ElevatorListenerService.registerEventExecutor(PluginInitializeEvent.class, EventPriority.NORMAL, this::onSSB2Enable);
    }

    public void onSSB2Enable(PluginInitializeEvent e) {
        try {
            IslandPrivilege.register(USE_ELEVATOR_FLAG);
            IslandPrivilege.register(EDIT_NAME_ELEVATOR_FLAG);
            IslandPrivilege.register(EDIT_SETTINGS_ELEVATOR_FLAG);
            USE_ELEVATOR = IslandPrivilege.getByName(USE_ELEVATOR_FLAG);
            EDIT_NAME_ELEVATOR = IslandPrivilege.getByName(EDIT_NAME_ELEVATOR_FLAG);
            EDIT_SETTINGS_ELEVATOR = IslandPrivilege.getByName(EDIT_SETTINGS_ELEVATOR_FLAG);
            Elevators.getElevatorsLogger().info("Hooked into SuperiorSkyblock2 correctly");
            registered = true;
        } catch(Exception ex) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to register SSB2 hook. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(ex));
        }
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        if(!registered)
            return true;

        Island island = SuperiorSkyblockAPI.getIslandAt(elevator.getLocation());
        if (island != null)
            return island.hasPermission(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()), USE_ELEVATOR);

        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        if(!registered) return null;
        Island island = SuperiorSkyblockAPI.getIslandAt(elevator.getLocation());
        if(island == null)
            return null;

        boolean flagEnabled = this.isCheckEnabled(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether island");
        lore.add(ChatColor.GRAY + "guests are blocked from");
        lore.add(ChatColor.GRAY + "using this Elevator.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "SuperiorSkyblock2", Material.DIAMOND, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
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
