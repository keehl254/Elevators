package me.keehl.elevators.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorActionSetting;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.actions.ElevatorAction;
import me.keehl.elevators.models.actions.ElevatorActionVariable;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class MessageAllAction extends ElevatorAction {

    private static final ElevatorActionVariable<String> messageGrouping = new ElevatorActionVariable<>("", x -> x, x -> MessageHelper.getLocaleComponent(x).serialize(), "message","m");

    public MessageAllAction(JavaPlugin plugin, IElevatorType elevatorType, String key) {
        super(plugin, elevatorType, key, messageGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the message broadcasted.";
        IElevatorActionSetting<String> messageSetting = this.mapSetting(messageGrouping, "message","Message", desc, Material.WRITABLE_BOOK, ChatColor.GOLD);
        messageSetting.onClick(this::editMessage);
    }

    @Override
    public void execute(IElevatorEventData eventData, Player player) {
        String componentText = this.getVariableValue(messageGrouping, eventData.getOrigin());
        ILocaleComponent component = MessageHelper.getLocaleComponent(componentText).getFormatted(eventData);
        for(Player otherPlayer : Bukkit.getOnlinePlayers()) {
            component.send(otherPlayer);
        }
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
        Elevators.getLocale().getEnterMessageMessage().send(player);
        input.start();
    }

}
