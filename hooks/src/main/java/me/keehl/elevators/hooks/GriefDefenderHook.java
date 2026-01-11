package me.keehl.elevators.hooks;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustTypes;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.hooks.ProtectionHook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GriefDefenderHook extends ProtectionHook {
    //TODO: Add in the configs the option for select the minimium rank for edit name and settings
    public GriefDefenderHook() {
        super("GriefDefender");
    }

    @Override
    public void onInit() {
    }

    @Override
    public boolean canPlayerUseElevator(Player player, IElevator elevator, boolean sendMessage) {
        final Claim claim = GriefDefender.getCore().getClaimAt(elevator.getLocation());

        if (claim == null || claim.isWilderness())
            return true;

        return claim.canUseBlock(elevator.getShulkerBox(), elevator.getLocation(), GriefDefender.getCore().getUser(player.getUniqueId()), TrustTypes.ACCESSOR);
    }

    @Override
    public boolean canEditName(Player player, IElevator elevator, boolean sendMessage) {
        final Claim claim = GriefDefender.getCore().getClaimAt(elevator.getLocation());

        if (claim == null)
            return true;
        if(claim.isWilderness())
            return true;
        return claim.canUseBlock(elevator.getShulkerBox(), elevator.getLocation(), GriefDefender.getCore().getUser(player.getUniqueId()), TrustTypes.ACCESSOR);
    }

    @Override
    public boolean canEditSettings(Player player, IElevator elevator, boolean sendMessage) {
        final Claim claim = GriefDefender.getCore().getClaimAt(elevator.getLocation());

        if (claim == null)
            return true;
        if(claim.isWilderness())
            return true;
        return claim.canUseBlock(elevator.getShulkerBox(), elevator.getLocation(), GriefDefender.getCore().getUser(player.getUniqueId()), TrustTypes.MANAGER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack createIconForElevator(Player player, IElevator elevator) {
        final Claim claim = GriefDefender.getCore().getClaimAt(elevator.getLocation());
        if(claim == null || claim.isWilderness()) return null;

        boolean flagEnabled = this.isCheckEnabled(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether claim");
        lore.add(ChatColor.GRAY + "guests are blocked from");
        lore.add(ChatColor.GRAY + "using this Elevator.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Grief Defender", Material.SHIELD, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, IElevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
        onReturn.run();
    }
}
