package me.keehl.elevators.services.hooks;

import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.ProtectionHook;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class GriefPreventionHook extends ProtectionHook {
    //TODO: Add in the configs the option for select the minimium rank for edit name and settings
    private final GriefPrevention griefPrevention;

    public GriefPreventionHook() {
        super("GriefPrevention");
        this.griefPrevention = (GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention");
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        if(!this.isCheckEnabled(elevator))
            return true;

        if(this.griefPrevention == null)
            return false;

        PlayerData playerData = this.griefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = this.griefPrevention.dataStore.getClaimAt(elevator.getLocation(), false, playerData.lastClaim);
        if (claim == null)
            return true;

        Supplier<String> message = claim.checkPermission(player, ClaimPermission.Access, null);
        if(message != null) {
            if(sendMessage)
                player.sendMessage(ChatColor.RED + message.get());
            return false;
        }

        return true;
    }

    @Override
    public boolean canEditName(Player player, Elevator elevator, boolean sendMessage) {
        if(this.griefPrevention == null)
            return false;
        PlayerData playerData = this.griefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = this.griefPrevention.dataStore.getClaimAt(elevator.getLocation(), false, playerData.lastClaim);
        if (claim == null)
            return true;

        Supplier<String> message = claim.checkPermission(player, ClaimPermission.Edit, null);
        if (message != null) {
            if (sendMessage)
                player.sendMessage(ChatColor.RED + message.get());
            return false;
        }
        return true;
    }

    @Override
    public boolean canEditSettings(Player player, Elevator elevator, boolean sendMessage) {
        if(this.griefPrevention == null)
            return false;
        PlayerData playerData = this.griefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = this.griefPrevention.dataStore.getClaimAt(elevator.getLocation(), false, playerData.lastClaim);
        if(claim == null)
            return true;

        Supplier<String> message = claim.checkPermission(player, ClaimPermission.Manage, null);
        if (message != null) {
            if (sendMessage)
                player.sendMessage(ChatColor.RED + message.get());
            return false;
        }
        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        if(this.griefPrevention == null)
            return null;

        PlayerData playerData = this.griefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = this.griefPrevention.dataStore.getClaimAt(elevator.getLocation(), false, playerData.lastClaim);
        if(claim == null)
            return null;

        boolean flagEnabled = this.isCheckEnabled(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether claim");
        lore.add(ChatColor.GRAY + "guests are blocked from");
        lore.add(ChatColor.GRAY + "using this Elevator.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Grief Prevention", Material.GOLDEN_SWORD, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
        onReturn.run();
    }
}
