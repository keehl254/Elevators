package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ProtectionHook;
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

public class GriefPreventionHook extends ProtectionHook {

    private final GriefPrevention griefPrevention;

    public GriefPreventionHook() {
        super("GriefPrevention");
        this.griefPrevention = (GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention");
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        if(this.shouldAllowGuestUse(elevator))
            return true;

        if(this.griefPrevention == null)
            return false;

        PlayerData playerData = this.griefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = this.griefPrevention.dataStore.getClaimAt(elevator.getLocation(), false, playerData.lastClaim);
        if (claim != null) {
            Supplier<String> message = claim.checkPermission(player, ClaimPermission.Access, null);
            if(message != null) {
                if(sendMessage)
                    player.sendMessage(ChatColor.RED + message.get());
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        if(this.griefPrevention == null) return null;

        PlayerData playerData = this.griefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = this.griefPrevention.dataStore.getClaimAt(elevator.getLocation(), false, playerData.lastClaim);
        if(claim == null) return null;

        boolean flagEnabled = this.shouldAllowGuestUse(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether non-members");
        lore.add(ChatColor.GRAY + "can use this elevator.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Grief Prevention", Material.GOLDEN_SWORD, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleAllowMemberUse(elevator);
        onReturn.run();
    }
}
