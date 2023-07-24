package com.lkeehl.elevators.services.hooks;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustTypes;
import com.lkeehl.elevators.models.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GriefDefenderHook implements ElevatorHook {


    @Override
    public boolean canPlayerUseElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        final Claim claim = GriefDefender.getCore().getClaimAt(box.getLocation());

        if (claim == null || claim.isWilderness())
            return true;

        return claim.canUseBlock(box, box.getLocation(), GriefDefender.getCore().getUser(player.getUniqueId()), TrustTypes.ACCESSOR);
    }

    @Override
    public ItemStack createIconForElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        return null;
    }
}
