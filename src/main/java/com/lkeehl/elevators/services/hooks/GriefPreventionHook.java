package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class GriefPreventionHook implements ElevatorHook {

    private final GriefPrevention griefPrevention;

    public GriefPreventionHook() {
        this.griefPrevention = (GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention");
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {

        if(this.griefPrevention == null)
            return false;

        ShulkerBox box = elevator.getShulkerBox();

        PlayerData playerData = this.griefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = this.griefPrevention.dataStore.getClaimAt(box.getLocation(), false, playerData.lastClaim);
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
    public ItemStack createIconForElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        return null;
    }
}
