package me.keehl.elevators.services.hooks;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.ProtectionHook;
import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * At the time of making this hook, the API does not have enough info to build on and the repo is not
 * up to date with the GitHub source. The documentation is also practically nonexistent. I will revisit
 * this in the future to update current cringey solutions with actual supported ones once the Protect
 * documentation is completed or further along.
*/
@SuppressWarnings("deprecation")
public class ProtectHook extends ProtectionHook {

    private final Flag<Boolean> useFlag;
    private final Flag<Boolean> nameFlag;
    private final Flag<Boolean> settingsFlag;

    private final ProtectPlugin protectPlugin;

    public ProtectHook() {
        super("Protect");

        this.protectPlugin = (ProtectPlugin) Bukkit.getPluginManager().getPlugin("Protect");
        this.useFlag = registerFlag("elevator_use");
        this.nameFlag = registerFlag("elevator_rename");
        this.settingsFlag = registerFlag("elevator_settings");
    }

    @Override
    public void onInit() {
    }

    private Flag<Boolean> registerFlag(@KeyPattern.Value String flagName) {
        NamespacedKey key = new NamespacedKey(Elevators.getInstance(), flagName);
        Optional<Flag<Boolean>> flagOpt = this.protectPlugin.flagRegistry().getFlag(key);
        return flagOpt.orElseGet(() ->
                this.protectPlugin.flagRegistry().register(Elevators.getInstance(), flagName, false)
        );

    }

    public void failed(Player player, String message) {
        MessageHelper.sendFormattedMessage(player,"<red><dark_gray>[<dark_red><bold>!</bold></dark_red>]</dark_gray> " + message + "</red>");
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        Area area = this.protectPlugin.areaProvider().getArea(elevator.getLocation());
        if(area.getFlag(this.useFlag))
            return true;

        boolean permitted = area.isPermitted(player.getUniqueId());
        if(!permitted && sendMessage)
            this.failed(player, "You are not allowed to do that here!");

        return permitted;
    }

    @Override
    public boolean canEditName(Player player, Elevator elevator, boolean sendMessage) {
        Area area = this.protectPlugin.areaProvider().getArea(elevator.getLocation());
        if(area.getFlag(this.nameFlag))
            return true;

        boolean permitted = area.isPermitted(player.getUniqueId());
        if(!permitted && sendMessage)
            this.failed(player, "You are not allowed to do that here!");

        return permitted;
    }

    @Override
    public boolean canEditSettings(Player player, Elevator elevator, boolean sendMessage) {
        Area area = this.protectPlugin.areaProvider().getArea(elevator.getLocation());
        if(area.getFlag(this.settingsFlag))
            return true;

        boolean permitted = area.isPermitted(player.getUniqueId());
        if(!permitted && sendMessage)
            this.failed(player, "You are not allowed to do that here!");

        return permitted;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        boolean flagEnabled = this.isCheckEnabled(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether this");
        lore.add(ChatColor.GRAY + "elevator will check");
        lore.add(ChatColor.GRAY + "Protect flags.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Protect", Material.SHIELD, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
        onReturn.run();
    }
}
