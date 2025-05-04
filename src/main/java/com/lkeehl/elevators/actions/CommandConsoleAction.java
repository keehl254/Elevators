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

public class CommandConsoleAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> commandGrouping = new ElevatorActionGrouping<>("", i -> i, "command","c");

    public CommandConsoleAction(ElevatorType elevatorType) {
        super(elevatorType, "command-console", "command", commandGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the command executed.";
        ElevatorActionSetting<String> commandSetting = this.mapSetting(commandGrouping, "command","Command", desc, Material.COMMAND_BLOCK, ChatColor.GOLD);
        commandSetting.onClick(this::editCommand);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getGroupingObject(commandGrouping, eventData.getOrigin()));
        value = MessageHelper.formatPlaceholders(player, value);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value);
    }

    private void editCommand(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(message -> {
            setValueMethod.accept(message);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterCommand);
        input.start();
    }

}
