package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.configuration.caption.Caption;
import com.plotsquared.core.configuration.caption.LocaleHolder;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.GlobalFlagContainer;
import com.plotsquared.core.plot.flag.types.BooleanFlag;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PlotSquaredHook implements ElevatorHook {

    private final ElevatorFlag flag;

    private final PlotAPI api;

    public PlotSquaredHook() {
        GlobalFlagContainer.getInstance().addFlag(this.flag = new ElevatorFlag(true));

        this.api = new PlotAPI();
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {

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
    public ItemStack createIconForElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        return null;
    }

    public class ElevatorFlag extends BooleanFlag<ElevatorFlag> {

        protected ElevatorFlag(boolean value) {
            super(value, new Caption() {
                @Override
                public @NonNull String getComponent(@NonNull LocaleHolder localeHolder) {
                    return "Set to `true` to allow guests to use elevators.";
                }
            });
        }

        @Override
        protected ElevatorFlag flagOf(Boolean aBoolean) {
            return new ElevatorFlag(aBoolean);
        }
    }

}
