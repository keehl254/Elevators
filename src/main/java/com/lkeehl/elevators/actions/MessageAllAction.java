package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ElevatorConfigService;
import com.lkeehl.elevators.services.interaction.SimpleInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

public class MessageAllAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> messageGrouping = new ElevatorActionGrouping<>("", i -> i, "message","m");

    public MessageAllAction(ElevatorType elevatorType) {
        super(elevatorType, "message-all","message", messageGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the message broadcasted.";
        ElevatorActionSetting<String> messageSetting = this.mapSetting(messageGrouping, "message","Message", desc, Material.WRITABLE_BOOK, ChatColor.GOLD);
        messageSetting.onClick(this::editMessage);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {

        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getGroupingObject(messageGrouping, eventData.getOrigin()));
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        for(Player otherPlayer : Bukkit.getOnlinePlayers())
            otherPlayer.sendMessage(value);
    }

    private void editMessage(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(message -> {
            setValueMethod.accept(message);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterMessage);
        input.start();
    }

}
