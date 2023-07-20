package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class ActionBarAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> messageGrouping = new ElevatorActionGrouping<>("", i -> i, "message","m");

    public ActionBarAction(ElevatorType elevatorType) {
        super(elevatorType, "action-bar", "message", messageGrouping);
    }

    @Override
    protected void onInitialize(String value) {

    }

    @Override
    public void execute(ShulkerBox origin, ShulkerBox destination, ElevatorType elevatorType, Player player) {
        String value = elevatorType.formatPlaceholders(player, from, to, BaseUtil.formatColors(this.value));
        value = PlaceHolders.request(player.getUniqueId(), value);

        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(value));
    }

    @Override
    public CompletableFuture<Boolean> openCreate(ElevatorType elevator, Player player, byte direction) {
        return new CompletableFuture<>();
    }
}
