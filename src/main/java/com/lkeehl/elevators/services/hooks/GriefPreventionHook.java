package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.models.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GriefPreventionHook implements ElevatorHook {

    private final GriefPrevention griefPrevention;

    public GriefPreventionHook() {
        this.griefPrevention = (GriefPrevention) Bukkit.getPluginManager().getPlugin("GriefPrevention");
    }

    @Override
    public boolean canPlayerUseElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {

        if(this.griefPrevention == null)
            return false;

        PlayerData playerData = this.griefPrevention.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = this.griefPrevention.dataStore.getClaimAt(box.getLocation(), false, playerData.lastClaim);
        if (claim != null)
            return claim.allowAccess(player) == null;

        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        return null;
    }
}
