package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.CompletableFuture;

public class ActionBarAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> messageGrouping = new ElevatorActionGrouping<>("", i -> i, "message","m");

    public ActionBarAction(ElevatorType elevatorType) {
        super(elevatorType, "action-bar", "message", messageGrouping);

        String desc = "This option controls the message shown in the action bar.";
        ElevatorActionSetting<String> setting = this.mapSetting(messageGrouping, "message","Message", desc, Material.WRITABLE_BOOK, ChatColor.GOLD);
        setting.setupDataStore("message", PersistentDataType.STRING);

        this.setIcon(ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Action Bar", Material.BELL, 1));
    }

    @Override
    protected void onInitialize(String value) {

    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getGroupingObject(messageGrouping));
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(value));
    }

}
