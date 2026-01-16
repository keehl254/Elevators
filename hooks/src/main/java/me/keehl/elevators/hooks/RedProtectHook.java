package me.keehl.elevators.hooks;

import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.hooks.ProtectionHook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class RedProtectHook extends ProtectionHook {
    //TODO: Code cleanup
    private final RedProtectAPI redProtect;

    private final String useFlag = "elevators-allow-use";
    private final String settingsFlagName = "elevators-allow-settings";

    public RedProtectHook() {
        super("RedProtect");
        this.redProtect = RedProtect.get().getAPI();

        this.redProtect.addFlag(this.useFlag, true, false);
        this.redProtect.addFlag(this.settingsFlagName, false, false);
    }

    @Override
    public void onInit() {
    }

    @Override
    public boolean canPlayerUseElevator(Player player, IElevator elevator, boolean sendMessage) {
        Region region = this.redProtect.getRegion(elevator.getShulkerBox().getLocation());
        if(region == null || region.getFlagBool(this.useFlag))
            return true;

        if(region.isLeader(player) || region.isAdmin(player) || region.isMember(player) || player.hasPermission("redprotect.flag.bypass." + this.useFlag))
            return true;

        if(sendMessage)
            player.sendMessage(ChatColor.RED + "You can't interact with this here!");
        return false;
    }

    @Override
    public boolean canEditName(Player player, IElevator elevator, boolean sendMessage) {
        return this.canEditSettings(player, elevator, sendMessage);
    }

    @Override
    public boolean canEditSettings(Player player, IElevator elevator, boolean sendMessage) {
        Region region = this.redProtect.getRegion(elevator.getShulkerBox().getLocation());
        if(region == null || region.getFlagBool(this.settingsFlagName))
            return true;

        //Regardless of sendMessage, RedProtect is going to send one anyway. Might as well let them do it.
        return region.isLeader(player) || region.isAdmin(player) || player.hasPermission("redprotect.flag.bypass." + this.settingsFlagName);
    }

    @Override
    public ItemStack createIconForElevator(Player player, IElevator elevator) {
        Region region = this.redProtect.getRegion(elevator.getLocation());
        if(region == null) return null;

        boolean flagEnabled = this.isCheckEnabled(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether this");
        lore.add(ChatColor.GRAY + "elevator respects the");
        lore.add(ChatColor.GRAY + "elevator-use claim flag.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "Red Protect", Material.RED_DYE, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, IElevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
        onReturn.run();
    }
}
