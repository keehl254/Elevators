package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.CompletableFuture;

public class MessageAllAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> messageGrouping = new ElevatorActionGrouping<>("", i -> i, "message","m");

    public MessageAllAction(ElevatorType elevatorType) {
        super(elevatorType, "message-all","message", messageGrouping);

        String desc = "This option controls the message broadcasted.";
        ElevatorActionSetting<String> messageSetting = this.mapSetting(messageGrouping, "message","Message", desc, Material.WRITABLE_BOOK, ChatColor.GOLD);
        messageSetting.setupDataStore("message", PersistentDataType.STRING);

        this.setIcon(ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "Message All", Material.ENCHANTED_BOOK, 1));
    }

    @Override
    protected void onInitialize(String value) {

    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {

        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getGroupingObject(messageGrouping));
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        for(Player otherPlayer : Bukkit.getOnlinePlayers())
            otherPlayer.sendMessage(value);
    }

}
