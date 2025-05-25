package me.keehl.elevators.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.ElevatorAction;
import me.keehl.elevators.models.ElevatorActionGrouping;
import me.keehl.elevators.models.ElevatorEventData;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.interaction.SimpleInput;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class ActionBarAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> messageGrouping = new ElevatorActionGrouping<>("", i -> i, "message","m");

    public ActionBarAction(ElevatorType elevatorType) {
        super(elevatorType, "action-bar", "message", messageGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the message shown in the action bar.";
        ElevatorActionSetting<String> setting = this.mapSetting(messageGrouping, "message","Message", desc, Material.WRITABLE_BOOK, ChatColor.GOLD);
        setting.setupDataStore("message", PersistentDataType.STRING);
        setting.onClick(this::editMessage);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, this.getGroupingObject(messageGrouping, eventData.getOrigin()));
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        player.sendActionBar(value);
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
