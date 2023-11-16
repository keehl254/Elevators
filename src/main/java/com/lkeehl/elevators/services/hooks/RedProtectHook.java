package com.lkeehl.elevators.services.hooks;

import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.hooks.ProtectionHook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RedProtectHook extends ProtectionHook {

    private final RedProtectAPI redProtect;

    private final String flagName = "outsiders-use-elevators";

    public RedProtectHook() {
        super("RedProtect");
        this.redProtect = RedProtect.get().getAPI();

        this.redProtect.addFlag("use-elevators", true, false);
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        if(!this.shouldDenyNonMemberUse(elevator))
            return true;

        Region region = redProtect.getRegion(elevator.getShulkerBox().getLocation());
        if(region == null || region.getFlagBool("outsiders-use-elevators"))
            return true;

        if(region.isLeader(player) || region.isAdmin(player) || region.isMember(player) || player.hasPermission("redprotect.flag.bypass." + this.flagName))
            return true;

        if(sendMessage)
            player.sendMessage(ChatColor.RED + "You can't interact with this here!");
        return false;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        Region region = redProtect.getRegion(elevator.getLocation());
        if(region == null) return null;

        boolean flagEnabled = !this.shouldDenyNonMemberUse(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether non-members");
        lore.add(ChatColor.GRAY + "can use this elevator.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "Red Protect", Material.RED_DYE, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleAllowMemberUse(elevator);
        onReturn.run();
    }
}
