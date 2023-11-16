package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class TitleAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> titleGrouping = new ElevatorActionGrouping<>("", i -> i, "title", "tit", "t");
    private static final ElevatorActionGrouping<String> subTitleGrouping = new ElevatorActionGrouping<>("", i -> i, "subtitle","sub","s");

    public TitleAction(ElevatorType elevatorType) {
        super(elevatorType, "title", "title", titleGrouping,subTitleGrouping);
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

    @Override
    public CompletableFuture<Boolean> openCreate(ElevatorType elevator, Player player, byte direction) {
        return null;
    }

    private String formatText(String message, ElevatorEventData eventData, Player player) {
        String value = MessageHelper.formatElevatorPlaceholders(player, eventData, message);
        value = MessageHelper.formatPlaceholders(player, value);
        value = MessageHelper.formatColors(value);

        return value;
    }
}
