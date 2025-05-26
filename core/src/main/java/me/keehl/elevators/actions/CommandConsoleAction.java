package me.keehl.elevators.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.ElevatorAction;
import me.keehl.elevators.models.ElevatorActionVariable;
import me.keehl.elevators.models.ElevatorEventData;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class CommandConsoleAction extends ElevatorAction {

    private static final ElevatorActionVariable<String> commandGrouping = new ElevatorActionVariable<>("", i -> i, "command","c");

    public CommandConsoleAction(ElevatorType elevatorType) {
        super(elevatorType, "command-console", commandGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the command executed.";
        ElevatorActionSetting<String> commandSetting = this.mapSetting(commandGrouping, "command","Command", desc, Material.COMMAND_BLOCK, ChatColor.GOLD);
        commandSetting.onClick(this::editCommand);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getVariableValue(commandGrouping, eventData.getOrigin()));
        value = MessageHelper.formatPlaceholders(player, value);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value);
    }

    private void editCommand(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        player.closeInventory();

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
