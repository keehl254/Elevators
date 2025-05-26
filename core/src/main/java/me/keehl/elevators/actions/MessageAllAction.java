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

public class MessageAllAction extends ElevatorAction {

    private static final ElevatorActionVariable<String> messageGrouping = new ElevatorActionVariable<>("", i -> i, "message","m");

    public MessageAllAction(ElevatorType elevatorType) {
        super(elevatorType, "message-all", messageGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the message broadcasted.";
        ElevatorActionSetting<String> messageSetting = this.mapSetting(messageGrouping, "message","Message", desc, Material.WRITABLE_BOOK, ChatColor.GOLD);
        messageSetting.onClick(this::editMessage);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {

        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getVariableValue(messageGrouping, eventData.getOrigin()));
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        for(Player otherPlayer : Bukkit.getOnlinePlayers())
            otherPlayer.sendMessage(value);
    }

    private void editMessage(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        player.closeInventory();

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
