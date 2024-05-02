package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MessagePlayerAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> messageGrouping = new ElevatorActionGrouping<>("", i -> i, "message","m");

    public MessagePlayerAction(ElevatorType elevatorType) {
        super(elevatorType, "message-player", "message",messageGrouping);

        String desc = "This option controls the message sent to the user of an elevator.";
        ElevatorActionSetting<String> setting = this.mapSetting(messageGrouping, "Message", desc, Material.WRITABLE_BOOK, ChatColor.GOLD);
        setting.setupDataStore("message", PersistentDataType.STRING);
        setting.onClick(this::editMessage);
    }

    @Override
    protected void onInitialize(String value) {

    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getGroupingObject(messageGrouping));
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        player.sendMessage(value);
    }

    private void editMessage(Player player, Runnable returnMethod, String currentValue, Consumer<String> setValueMethod) {
        returnMethod.run();
    }

}
