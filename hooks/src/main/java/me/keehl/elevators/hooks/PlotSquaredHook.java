package me.keehl.elevators.hooks;

import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.ProtectionHook;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// Gotta be honest, I think that PlotSquared has one of the most inconvenient and convoluted flag systems ever.
@SuppressWarnings("deprecation")
public class PlotSquaredHook extends ProtectionHook {

    private final ElevatorUseFlag useFlag;
    private final ElevatorEditNameFlag nameFlag;
    private final ElevatorEditSettingsFlag settingsFlag;

    private final PlotAPI api;

    public PlotSquaredHook() {
        super("PlotSquared");
        GlobalFlagContainer.getInstance().addFlag(this.useFlag = new ElevatorUseFlag(true));
        GlobalFlagContainer.getInstance().addFlag(this.nameFlag = new ElevatorEditNameFlag(true));
        GlobalFlagContainer.getInstance().addFlag(this.settingsFlag = new ElevatorEditSettingsFlag(false));

        this.api = new PlotAPI();
    }

    @Override
    public void onInit() {
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        PlotPlayer<?> plotPlayer = this.api.wrapPlayer(player.getUniqueId());
        if(plotPlayer == null)
            return false;
        Plot currentPlot = plotPlayer.getCurrentPlot();
        if(currentPlot == null || currentPlot.getFlag(this.useFlag))
            return true;

        if(currentPlot.getTrusted().contains(player.getUniqueId()))
            return true;

        if(currentPlot.getMembers().contains(player.getUniqueId()))
            return true;

        return currentPlot.getOwners().contains(player.getUniqueId());
    }

    @Override
    public boolean canEditName(Player player, Elevator elevator, boolean sendMessage) {
        PlotPlayer<?> plotPlayer = this.api.wrapPlayer(player.getUniqueId());
        if(plotPlayer == null)
            return false;
        Plot currentPlot = plotPlayer.getCurrentPlot();
        if(currentPlot == null || currentPlot.getFlag(this.nameFlag))
            return true;

        if(currentPlot.getTrusted().contains(player.getUniqueId()))
            return true;

        if(currentPlot.getMembers().contains(player.getUniqueId()))
            return true;

        return currentPlot.getOwners().contains(player.getUniqueId());
    }

    @Override
    public boolean canEditSettings(Player player, Elevator elevator, boolean sendMessage) {
        PlotPlayer<?> plotPlayer = this.api.wrapPlayer(player.getUniqueId());
        if(plotPlayer == null)
            return false;
        Plot currentPlot = plotPlayer.getCurrentPlot();
        if(currentPlot == null || currentPlot.getFlag(this.settingsFlag))
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

        boolean flagEnabled = this.isCheckEnabled(elevator);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Controls whether plot");
        lore.add(ChatColor.GRAY + "guests are blocked from");
        lore.add(ChatColor.GRAY + "using this Elevator.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Status: ");
        lore.add(flagEnabled ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );

        return ItemStackHelper.createItem(ChatColor.AQUA + "" + ChatColor.BOLD + "Plot Squared", Material.DIAMOND_PICKAXE, 1, lore);
    }

    @Override
    public void onProtectionClick(Player player, Elevator elevator, Runnable onReturn) {
        this.toggleCheckEnabled(elevator);
        onReturn.run();
    }

    public static class ElevatorUseFlag extends BooleanFlag<ElevatorUseFlag> {

        protected ElevatorUseFlag(boolean value) {
            super(value, TranslatableCaption.of("flags.guest_elevators"));
        }

        @Override
        protected ElevatorUseFlag flagOf(@NotNull Boolean aBoolean) {
            return new ElevatorUseFlag(aBoolean);
        }
    }

    public static class ElevatorEditNameFlag extends BooleanFlag<ElevatorEditNameFlag> {

        protected ElevatorEditNameFlag(boolean value) {
            super(value, TranslatableCaption.of("flags.name_elevators"));
        }

        @Override
        protected ElevatorEditNameFlag flagOf(@NotNull Boolean aBoolean) {
            return new ElevatorEditNameFlag(aBoolean);
        }
    }

    public static class ElevatorEditSettingsFlag extends BooleanFlag<ElevatorEditSettingsFlag> {

        protected ElevatorEditSettingsFlag(boolean value) {
            super(value, TranslatableCaption.of("flags.settings_elevators"));
        }

        @Override
        protected ElevatorEditSettingsFlag flagOf(@NotNull Boolean aBoolean) {
            return new ElevatorEditSettingsFlag(aBoolean);
        }
    }

}
