package me.keehl.elevators.hooks;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.enums.FlagTarget;
import me.angeschossen.lands.api.flags.enums.RoleFlagCategory;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.ProtectionHook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class LandsHook extends ProtectionHook {

    private RoleFlag useFlag;
    private RoleFlag settingsFlag;

    private final LandsIntegration lands;

    public LandsHook() {
        super("Lands");

        this.lands = LandsIntegration.of(Elevators.getInstance());

        this.lands.onLoad(() -> {
            this.useFlag = RoleFlag.of(this.lands, FlagTarget.PLAYER, RoleFlagCategory.ACTION, "elevator_use");
            this.useFlag.setDisplayName("Elevator Use");
            this.useFlag.setDescription("Allows the role to use Elevators in this area");
            this.useFlag.setIcon(ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "Elevator Use", Material.RED_SHULKER_BOX,1,MessageHelper.formatLore("Allows the rule to use Elevators in this area", ChatColor.GRAY)));
            this.useFlag.setAlwaysAllowInWilderness(true);

            this.settingsFlag = RoleFlag.of(this.lands, FlagTarget.PLAYER, RoleFlagCategory.ACTION, "elevator_settings");
            this.settingsFlag.setDisplayName("Elevator Manage");
            this.settingsFlag.setDescription("Allows the role to edit Elevators in this area");
            this.settingsFlag.setIcon(ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Elevator Manage", Material.LIGHT_GRAY_SHULKER_BOX,1,MessageHelper.formatLore("Allows the rule to edit Elevators in this area", ChatColor.GRAY)));
        });
    }

    @Override
    public void onInit() {
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        LandWorld world = this.lands.getWorld(elevator.getLocation().getWorld());
        if(world == null)
            return true; // Lands is not enabled in this world

        return world.hasRoleFlag(this.lands.getLandPlayer(player.getUniqueId()), elevator.getLocation(), this.useFlag, null, sendMessage);
    }

    @Override
    public boolean canEditName(Player player, Elevator elevator, boolean sendMessage) {
        LandWorld world = this.lands.getWorld(elevator.getLocation().getWorld());
        if(world == null)
            return true; // Lands is not enabled in this world

        return world.hasRoleFlag(this.lands.getLandPlayer(player.getUniqueId()), elevator.getLocation(), this.settingsFlag, null, sendMessage);
    }

    @Override
    public boolean canEditSettings(Player player, Elevator elevator, boolean sendMessage) {
        LandWorld world = this.lands.getWorld(elevator.getLocation().getWorld());
        if(world == null)
            return true; // Lands is not enabled in this world

        return world.hasRoleFlag(this.lands.getLandPlayer(player.getUniqueId()), elevator.getLocation(), this.settingsFlag, null, sendMessage);
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        boolean flagEnabled = this.isCheckEnabled(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether this");
        lore.add(ChatColor.GRAY + "elevator will check");
        lore.add(ChatColor.GRAY + "Land flags for use.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Lands", Material.SHIELD, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
        onReturn.run();
    }
}
