package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.helpers.InventoryHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.services.DataContainerService;
import com.lkeehl.elevators.services.interaction.SimpleInput;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TitleAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> titleGrouping = new ElevatorActionGrouping<>("", i -> i, "title", "tit", "t");
    private static final ElevatorActionGrouping<String> subTitleGrouping = new ElevatorActionGrouping<>("", i -> i, "subtitle","sub","s");

    public TitleAction(ElevatorType elevatorType) {
        super(elevatorType, "title", "title", titleGrouping,subTitleGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the top text that appears in the middle of the screen upon elevator use.";
        ElevatorActionSetting<String> titleSetting = this.mapSetting(titleGrouping, "title","Title", desc, Material.PAPER, ChatColor.GOLD);
        titleSetting.onClick(this::editTitle);

        desc = "This option controls the bottom text that appears in the middle of the screen upon elevator use.";
        ElevatorActionSetting<String> subTitleSetting = this.mapSetting(subTitleGrouping, "subtitle","Sub-Title", desc, Material.NAME_TAG, ChatColor.YELLOW);
        subTitleSetting.onClick(this::editSubTitle);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String title = formatText(this.getGroupingObject(titleGrouping, eventData.getOrigin()), eventData, player);
        String subTitle = formatText(this.getGroupingObject(subTitleGrouping, eventData.getOrigin()), eventData, player);

        player.sendTitle(title, subTitle, 10, 70, 20);
    }

    private String formatText(String message, ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, message);
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        return value;
    }

    private void editTitle(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(message -> {
            setValueMethod.accept(message);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        MessageHelper.sendFormattedMessage(player, ConfigService.getRootConfig().locale.enterTitle);
        input.start();
    }

    private void editSubTitle(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(message -> {
            setValueMethod.accept(message);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        MessageHelper.sendFormattedMessage(player, ConfigService.getRootConfig().locale.enterSubtitle);
        input.start();
    }

}
