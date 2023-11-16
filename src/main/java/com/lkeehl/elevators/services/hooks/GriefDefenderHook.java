package com.lkeehl.elevators.services.hooks;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustTypes;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.hooks.ProtectionHook;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GriefDefenderHook extends ProtectionHook {

    public GriefDefenderHook() {
        super("GriefDefender");
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        if(!this.shouldDenyNonMemberUse(elevator))
            return true;

        ShulkerBox box = elevator.getShulkerBox();
        final Claim claim = GriefDefender.getCore().getClaimAt(elevator.getLocation());

        if (claim == null || claim.isWilderness())
            return true;

        return claim.canUseBlock(elevator.getShulkerBox(), elevator.getLocation(), GriefDefender.getCore().getUser(player.getUniqueId()), TrustTypes.ACCESSOR);
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        final Claim claim = GriefDefender.getCore().getClaimAt(elevator.getLocation());
        if(claim == null || claim.isWilderness()) return null;

        boolean flagEnabled = !this.shouldDenyNonMemberUse(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether non-members");
        lore.add(ChatColor.GRAY + "can use this elevator.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Grief Defender", Material.SHIELD, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleAllowMemberUse(elevator);
        onReturn.run();
    }
}
