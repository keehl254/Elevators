package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BossBarAction extends ElevatorAction {


    private static final ElevatorActionGrouping<BarColor> barColorGrouping = new ElevatorActionGrouping<>(BarColor.BLUE, BarColor::valueOf, "barcolor","color","c");
    private static final ElevatorActionGrouping<BarStyle> barStyleGrouping = new ElevatorActionGrouping<>(BarStyle.SOLID, BarStyle::valueOf, "barstyle","style","s");
    private static final ElevatorActionGrouping<String> messageGrouping = new ElevatorActionGrouping<>("", i -> i, "message","m");

    public BossBarAction(ElevatorType elevatorType) {
        super(elevatorType, "boss-bar", "message", barColorGrouping, barStyleGrouping, messageGrouping);
    }

    @Override
    protected void onInitialize(String value) {

    }

    @Override
    public void execute(ShulkerBox origin, ShulkerBox destination, ElevatorType elevatorType, Player player) {
        /*if (elevator instanceof PremiumElevator && ((PremiumElevator) elevator).getSpeed() > 0.0)
            return;*/
        String message = BaseUtil.formatColors(elevator.formatPlaceholders(player, origin, destination, this.getGroupingObject(messageGrouping)));
        this.displayMessage(player, () -> message, (1.0F / (BaseElevators.getNMS().getFloorCount(destination, elevatorType) - 1)) * (BaseElevators.getNMS().getFloorNumber(destination, elevatorType) - 1), 30);
    }

    @Override
    public CompletableFuture<Boolean> openCreate(ElevatorType elevator, Player player, byte direction) {
        return null;
    }


    public BossBar getPlayerBar(Player player) {
        if (!player.hasMetadata("elevator-boss-bar")) {
            BossBar bar = Bukkit.createBossBar("elevator-boss-bar", this.getGroupingObject(barColorGrouping), this.getGroupingObject(barStyleGrouping));
            player.setMetadata("elevator-boss-bar", new FixedMetadataValue(Main.getInstance(), bar));
        }
        return (BossBar) player.getMetadata("elevator-boss-bar").get(0).value();
    }

    public String getMessage() {
        return this.message;
    }

    public void changeProgress(Player player, double progress) {
        if (((Double) progress).isNaN())
            progress = 0.0;
        else if (((Double) progress).isInfinite())
            progress = 1.0;
        this.getPlayerBar(player).setProgress(Math.max(0.0, Math.min(progress, 1.0)));
    }

    public void displayMessage(Player player, Supplier<String> message, double progress, int ticks) {
        if (((Double) progress).isNaN())
            progress = 0.0;
        else if (((Double) progress).isInfinite())
            progress = 1.0;

        BossBar bar = this.getPlayerBar(player);
        bar.setColor(this.getGroupingObject(barColorGrouping));
        bar.setStyle(this.getGroupingObject(barStyleGrouping));
        bar.setTitle(message.get());
        bar.setProgress(Math.max(0.0, Math.min(progress, 1.0)));
        if (!bar.getPlayers().contains(player))
            bar.addPlayer(player);

        if (player.hasMetadata("elevators-bossbar-seed"))
            player.removeMetadata("elevators-bossbar-seed", Main.getInstance());

        final long seed = BaseUtil.getRandom().nextLong();
        player.setMetadata("elevators-bossbar-seed", new FixedMetadataValue(Main.getInstance(), seed));
        bar.setVisible(true);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (player.getMetadata("elevators-bossbar-seed").get(0).asLong() == seed)
                bar.setVisible(false);
        }, ticks);
    }
}
