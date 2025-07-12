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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class TitleAction extends ElevatorAction {

    private static final ElevatorActionVariable<String> titleGrouping = new ElevatorActionVariable<>("", i -> i, "title", "tit", "t");
    private static final ElevatorActionVariable<String> subTitleGrouping = new ElevatorActionVariable<>("", i -> i, "subtitle","sub","s");

    public TitleAction(JavaPlugin plugin, ElevatorType elevatorType, String key) {
        super(plugin, elevatorType, key, titleGrouping,subTitleGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the top text that appears in the middle of the screen upon elevator use.";
        ElevatorActionSetting<String> titleSetting = this.mapSetting(titleGrouping, "title","Title", desc, Material.PAPER, ChatColor.GOLD, true);
        titleSetting.onClick(this::editTitle);

        desc = "This option controls the bottom text that appears in the middle of the screen upon elevator use.";
        ElevatorActionSetting<String> subTitleSetting = this.mapSetting(subTitleGrouping, "subtitle","Sub-Title", desc, Material.NAME_TAG, ChatColor.YELLOW, true);
        subTitleSetting.onClick(this::editSubTitle);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String title = formatText(this.getVariableValue(titleGrouping, eventData.getOrigin()), eventData, player);
        String subTitle = formatText(this.getVariableValue(subTitleGrouping, eventData.getOrigin()), eventData, player);

        player.sendTitle(title, subTitle, 10, 70, 20);
    }

    private String formatText(String message, ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, message);
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatLineColors(value);

        return value;
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
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterTitle);
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
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterSubtitle);
        input.start();
    }

}
