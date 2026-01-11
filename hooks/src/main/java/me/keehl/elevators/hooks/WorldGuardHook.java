package me.keehl.elevators.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.hooks.ProtectionHook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class WorldGuardHook extends ProtectionHook {

    private static StateFlag USE_FLAG;
    private static StateFlag NAME_FLAG;
    private static StateFlag SETTINGS_FLAG;

    public WorldGuardHook() {
        super("WorldGuard");

        USE_FLAG = registerFlag("elevators_allow_use");
        NAME_FLAG = registerFlag("elevators_allow_rename");
        SETTINGS_FLAG = registerFlag("elevators_allow_settings");
    }

    @Override
    public void onInit() {
    }

    private StateFlag registerFlag(String flagName) {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("elevators-allow-use", true);
            registry.register(flag);
            return flag;
        } catch (FlagConflictException e) {
            return (StateFlag) registry.get("elevators-allow-use");
        }
    }

    private void formatAndSendDenyMessage(String what, LocalPlayer localPlayer, String message) {
        if (message == null || message.isEmpty()) return;
        message = WorldGuard.getInstance().getPlatform().getMatcher().replaceMacros(localPlayer, message);
        message = CommandUtils.replaceColorMacros(message);
        localPlayer.printRaw(message.replace("%what%", what));
    }

    @Override
    public boolean canPlayerUseElevator(Player player, IElevator elevator, boolean sendMessage) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        Location location = BukkitAdapter.adapt(elevator.getLocation());
        boolean allowed = query.testState(location, localPlayer, USE_FLAG);

        if(!allowed && sendMessage) {
            String message = query.queryValue(location, localPlayer, Flags.DENY_MESSAGE);
            this.formatAndSendDenyMessage("use elevators", localPlayer, message);
        }

        return allowed;
    }

    @Override
    public boolean canEditName(Player player, IElevator elevator, boolean sendMessage) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        Location location = BukkitAdapter.adapt(elevator.getLocation());
        boolean allowed = query.testState(location, localPlayer, NAME_FLAG);

        if(!allowed && sendMessage) {
            String message = query.queryValue(location, localPlayer, Flags.DENY_MESSAGE);
            this.formatAndSendDenyMessage("rename elevators", localPlayer, message);
        }
        return allowed;
    }

    @Override
    public boolean canEditSettings(Player player, IElevator elevator, boolean sendMessage) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        Location location = BukkitAdapter.adapt(elevator.getLocation());
        boolean allowed = query.testState(location, localPlayer, SETTINGS_FLAG);

        if(!allowed && sendMessage) {
            String message = query.queryValue(location, localPlayer, Flags.DENY_MESSAGE);
            this.formatAndSendDenyMessage("edit elevator settings", localPlayer, message);
        }
        return allowed;
    }

    @Override
    public ItemStack createIconForElevator(Player player, IElevator elevator) {
        boolean flagEnabled = this.isCheckEnabled(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether this");
        lore.add(ChatColor.GRAY + "elevator will check");
        lore.add(ChatColor.GRAY + "World Guard flags.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "World Guard", Material.LEAD, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, IElevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
        onReturn.run();
    }
}
