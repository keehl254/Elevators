package me.keehl.elevators.hooks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.PluginInitializeEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.hooks.ProtectionHook;
import me.keehl.elevators.api.services.IElevatorListenerService;
import me.keehl.elevators.api.services.configs.versions.DefaultConfigHookData;
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
public class SuperiorSkyblock2Hook extends ProtectionHook<DefaultConfigHookData> {

    private static IslandPrivilege USE_ELEVATOR, SETTINGS_FLAG;
    private static boolean registered = false;

    public SuperiorSkyblock2Hook() {
        super("SuperiorSkyblock2", false, new DefaultConfigHookData());

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

        final String USE_NAME = "elevators_use";
        final String SETTINGS_NAME = "elevators_settings";
        try {
            /*
                It hurts me inside that this API doesn't return the IslandPrivilege being setup and that we cannot
                setup a default icon / role for the flags.
             */
            IslandPrivilege.register(USE_NAME);
            IslandPrivilege.register(SETTINGS_NAME);

            USE_ELEVATOR = IslandPrivilege.getByName(USE_NAME);
            SETTINGS_FLAG = IslandPrivilege.getByName(SETTINGS_NAME);

            ElevatorsAPI.log(Level.INFO, "Hooked into SuperiorSkyblock2.");
            ElevatorsAPI.log("");
            ElevatorsAPI.log("The SuperiorSkyblock2 protection allows guest use by default. This is due to SSB2's flag");
            ElevatorsAPI.log("system not allowing me to register default permissions, roles, and icons. You can change");
            ElevatorsAPI.log("this in the Elevators config.");
            ElevatorsAPI.log("");
            ElevatorsAPI.log("The SSB2 flags are: elevators_use, elevators_settings.");
            ElevatorsAPI.log("To allow players to alter them through /island permissions, you must setup the flags in");
            ElevatorsAPI.log("in permissions.yml of the SSB2 menus folder. Likewise, assign the flags to a default role");
            ElevatorsAPI.log("in roles.yml, otherwise it defaults to \"Leader\".");
            ElevatorsAPI.log("");
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
        return this.canEditSettings(player, elevator, sendMessage);
    }

    @Override
    public boolean canEditSettings(Player player, IElevator elevator, boolean sendMessage) {
        if(!registered) return true;
        Island island = SuperiorSkyblockAPI.getIslandAt(elevator.getLocation());
        if (island != null) {
            return island.hasPermission(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()), SETTINGS_FLAG);
        }
        return false;
    }
}
