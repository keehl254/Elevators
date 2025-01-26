package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ProtectionHook;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.configuration.caption.TranslatableCaption;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.GlobalFlagContainer;
import com.plotsquared.core.plot.flag.types.BooleanFlag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

// Gotta be honest, I think that PlotSquared has one of the most inconvenient and convoluted flag systems ever.
public class PlotSquaredHook extends ProtectionHook {

    private final ElevatorFlag flag;

    private final PlotAPI api;

    public PlotSquaredHook() {
        super("PlotSquared");
        GlobalFlagContainer.getInstance().addFlag(this.flag = new ElevatorFlag(true));

        this.api = new PlotAPI();
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        if(this.shouldAllowGuestUse(elevator))
            return true;

        PlotPlayer<?> plotPlayer = this.api.wrapPlayer(player.getUniqueId());
        if(plotPlayer == null)
            return false;
        Plot currentPlot = plotPlayer.getCurrentPlot();
        if(currentPlot == null || currentPlot.getFlag(flag))
            return true;

        if(currentPlot.getTrusted().contains(player.getUniqueId()))
            return true;

        if(currentPlot.getMembers().contains(player.getUniqueId()))
            return true;

        return currentPlot.getOwners().contains(player.getUniqueId());
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {

        PlotPlayer<?> plotPlayer = this.api.wrapPlayer(player.getUniqueId());
        if(plotPlayer == null)  return null;

        Plot currentPlot = plotPlayer.getCurrentPlot();
        if(currentPlot == null) return null;

        boolean flagEnabled = this.shouldAllowGuestUse(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether non-members");
        lore.add(ChatColor.GRAY + "can use this elevator.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Squared", Material.DIAMOND_PICKAXE, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleAllowMemberUse(elevator);
        onReturn.run();
    }

    public static class ElevatorFlag extends BooleanFlag<ElevatorFlag> {

        protected ElevatorFlag(boolean value) {
            super(value, TranslatableCaption.of("flags.guest_elevators"));
        }

        @Override
        protected ElevatorFlag flagOf(Boolean aBoolean) {
            return new ElevatorFlag(aBoolean);
        }
    }

}
