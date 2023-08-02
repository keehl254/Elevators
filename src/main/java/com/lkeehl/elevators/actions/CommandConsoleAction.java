package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class CommandConsoleAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> commandGrouping = new ElevatorActionGrouping<>("", i -> i, "command","c");

    public CommandConsoleAction(ElevatorType elevatorType) {
        super(elevatorType, "command-console", "command", commandGrouping);
    }

    @Override
    protected void onInitialize(String value) {

    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getGroupingObject(commandGrouping));
        value = MessageHelper.formatPlaceholders(player, value);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value);
    }

    @Override
    public CompletableFuture<Boolean> openCreate(ElevatorType elevatorType, Player player, byte direction) {
        return new CompletableFuture<>();
    }
}
