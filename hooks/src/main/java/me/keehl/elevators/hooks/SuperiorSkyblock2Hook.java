package me.keehl.elevators.hooks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.PluginInitializeEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.hooks.ProtectionHook;
import me.keehl.elevators.api.services.IElevatorListenerService;
import org.bukkit.Bukkit;
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

        IElevatorListenerService listenerService = Bukkit.getServicesManager().load(IElevatorListenerService.class);
        if(listenerService == null) {
            ElevatorsAPI.log(Level.WARNING, "Elevator Services not been setup yet. SuperiorSkyblock2 hook may not function.");
            return;
        }

        listenerService.registerEventExecutor(PluginInitializeEvent.class, EventPriority.NORMAL, this::onSSB2Enable);
    }

    @Override
    public void onInit() {
    }

    public void onSSB2Enable(PluginInitializeEvent e) {
        try {
            IslandPrivilege.register(USE_ELEVATOR_FLAG);
            IslandPrivilege.register(EDIT_NAME_ELEVATOR_FLAG);
            IslandPrivilege.register(EDIT_SETTINGS_ELEVATOR_FLAG);
            USE_ELEVATOR = IslandPrivilege.getByName(USE_ELEVATOR_FLAG);
            EDIT_NAME_ELEVATOR = IslandPrivilege.getByName(EDIT_NAME_ELEVATOR_FLAG);
            EDIT_SETTINGS_ELEVATOR = IslandPrivilege.getByName(EDIT_SETTINGS_ELEVATOR_FLAG);
            ElevatorsAPI.log(Level.INFO, "Hooked into SuperiorSkyblock2 correctly");
            registered = true;
        } catch(Exception ex) {
            ElevatorsAPI.log(Level.WARNING, "Failed to register SSB2 hook. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n", ex);
        }
    }

    @Override
    public boolean canPlayerUseElevator(Player player, IElevator elevator, boolean sendMessage) {
        if(!registered)
            return true;

        Island island = SuperiorSkyblockAPI.getIslandAt(elevator.getLocation());
        if (island != null)
            return island.hasPermission(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()), USE_ELEVATOR);

        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, IElevator elevator) {
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
    public void onProtectionClick(Player player, IElevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
        onReturn.run();
    }

    @Override
    public boolean canEditName(Player player, IElevator elevator, boolean sendMessage) {
        if(!registered) return true;
        Island island = SuperiorSkyblockAPI.getIslandAt(elevator.getLocation());
        if (island != null) {
            return island.hasPermission(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()), EDIT_NAME_ELEVATOR);
        }
        return true;
    }

    @Override
    public boolean canEditSettings(Player player, IElevator elevator, boolean sendMessage) {
        if(!registered) return true;
        Island island = SuperiorSkyblockAPI.getIslandAt(elevator.getLocation());
        if (island != null) {
            return island.hasPermission(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()), EDIT_SETTINGS_ELEVATOR);
        }
        return false;
    }
}
