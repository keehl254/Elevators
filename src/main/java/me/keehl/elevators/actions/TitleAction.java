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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class TitleAction extends ElevatorAction {

    private static final ElevatorActionVariable<String> titleGrouping = new ElevatorActionVariable<>("", i -> i, x -> MessageHelper.getLocaleComponent(x).serialize(), "title", "tit", "t");
    private static final ElevatorActionVariable<String> subTitleGrouping = new ElevatorActionVariable<>("", i -> i, x -> MessageHelper.getLocaleComponent(x).serialize(), "subtitle","sub","s");

    public TitleAction(JavaPlugin plugin, IElevatorType elevatorType, String key) {
        super(plugin, elevatorType, key, titleGrouping,subTitleGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the top text that appears in the middle of the screen upon elevator use.";
        IElevatorActionSetting<String> titleSetting = this.mapSetting(titleGrouping, "title","Title", desc, Material.PAPER, ChatColor.GOLD, true);
        titleSetting.onClick(this::editTitle);

        desc = "This option controls the bottom text that appears in the middle of the screen upon elevator use.";
        IElevatorActionSetting<String> subTitleSetting = this.mapSetting(subTitleGrouping, "subtitle","Sub-Title", desc, Material.NAME_TAG, ChatColor.YELLOW, true);
        subTitleSetting.onClick(this::editSubTitle);
    }

    @Override
    public void execute(IElevatorEventData eventData, Player player) {
        ILocaleComponent title = formatText(this.getVariableValue(titleGrouping, eventData.getOrigin()), eventData, player);
        ILocaleComponent subTitle = formatText(this.getVariableValue(subTitleGrouping, eventData.getOrigin()), eventData, player);

        player.sendTitle(title.toLegacyText(), subTitle.toLegacyText(), 10, 70, 20);
    }

    private ILocaleComponent formatText(String message, IElevatorEventData eventData, Player player) {
        return MessageHelper.getLocaleComponent(message).getFormatted(eventData);
    }

    private void editTitle(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(message -> {
            setValueMethod.accept(message);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        Elevators.getLocale().getEnterTitleMessage().send(player);
        input.start();
    }

    private void editSubTitle(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(message -> {
            setValueMethod.accept(message);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        Elevators.getLocale().getEnterSubtitleMessage().send(player);
        input.start();
    }

}
