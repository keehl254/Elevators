package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.CompletableFuture;

public class CommandPlayerAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> commandGrouping = new ElevatorActionGrouping<>("", i -> i, "command","c");

    public CommandPlayerAction(ElevatorType elevatorType) {
        super(elevatorType, "command-player", "command", commandGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the command executed.";
        ElevatorActionSetting<String> commandSetting = this.mapSetting(commandGrouping, "command","Command", desc, Material.COMMAND_BLOCK, ChatColor.GOLD);
        commandSetting.setupDataStore("command", PersistentDataType.STRING);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getGroupingObject(commandGrouping, eventData.getOrigin()));
        value = MessageHelper.formatPlaceholders(player, value);

        player.performCommand(value);
    }

}
