package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TitleAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> titleGrouping = new ElevatorActionGrouping<>("", i -> i, "title", "tit", "t");
    private static final ElevatorActionGrouping<String> subTitleGrouping = new ElevatorActionGrouping<>("", i -> i, "subtitle","sub","s");

    public TitleAction(ElevatorType elevatorType) {
        super(elevatorType, "title", "title", titleGrouping,subTitleGrouping);

        String desc = "This option controls the top text that appears in the middle of the screen upon elevator use.";
        this.mapSetting(titleGrouping, "Title", desc, Material.PAPER, ChatColor.GOLD).onClick(this::editTitle);

        desc = "This option controls the bottom text that appears in the middle of the screen upon elevator use.";
        this.mapSetting(subTitleGrouping, "Sub-Title", desc, Material.NAME_TAG, ChatColor.YELLOW).onClick(this::editSubTitle);
    }

    @Override
    protected void onInitialize(String value) {

    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        String title = formatText(this.getGroupingObject(titleGrouping), eventData, player);
        String subTitle = formatText(this.getGroupingObject(subTitleGrouping), eventData, player);

        player.sendTitle(title, subTitle, 10, 70, 20);
    }

    private String formatText(String message, ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, message);
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        return value;
    }

    private void editTitle(Player player, Runnable returnMethod, String currentValue, Consumer<String> setValueMethod) {
        returnMethod.run();
    }

    private void editSubTitle(Player player, Runnable returnMethod, String currentValue, Consumer<String> setValueMethod) {
        returnMethod.run();
    }

}
