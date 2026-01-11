package me.keehl.elevators.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorActionSetting;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.actions.ElevatorAction;
import me.keehl.elevators.models.actions.ElevatorActionVariable;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class ActionBarAction extends ElevatorAction {

    private static final ElevatorActionVariable<String> messageGrouping = new ElevatorActionVariable<>("", i -> i, x -> MessageHelper.getLocaleComponent(x).serialize(), "message","m");

    public ActionBarAction(JavaPlugin plugin, IElevatorType elevatorType, String key) {
        super(plugin, elevatorType, key, messageGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the message shown in the action bar.";
        IElevatorActionSetting<String> setting = this.mapSetting(messageGrouping, "message","Message", desc, Material.WRITABLE_BOOK, ChatColor.GOLD, true);
        setting.onClick(this::editMessage);
    }

    @Override
    public void execute(IElevatorEventData eventData, Player player) {
        String message = this.getVariableValue(messageGrouping, eventData.getOrigin());
        message = MessageHelper.getLocaleComponent(message).getFormatted(eventData).toLegacyText();
        player.sendActionBar(message);
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
