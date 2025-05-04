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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class MessagePlayerAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> messageGrouping = new ElevatorActionGrouping<>("", i -> i, "message","m");

    public MessagePlayerAction(ElevatorType elevatorType) {
        super(elevatorType, "message-player", "message",messageGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the message sent to the user of an elevator.";
        ElevatorActionSetting<String> setting = this.mapSetting(messageGrouping, "message","Message", desc, Material.WRITABLE_BOOK, ChatColor.GOLD);
        setting.setupDataStore("message", PersistentDataType.STRING);
        setting.onClick(this::editMessage);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getGroupingObject(messageGrouping, eventData.getOrigin()));
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        player.sendMessage(value);
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
