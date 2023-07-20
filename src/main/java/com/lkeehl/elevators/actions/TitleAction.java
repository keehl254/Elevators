package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorType;
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
    public void execute(ShulkerBox origin, ShulkerBox destination, ElevatorType elevatorType, Player player) {
        String title = elevator.formatPlaceholders(player, origin, destination, BaseUtil.formatColors(this.getGroupingObject(titleGrouping)));
        String subTitle = elevator.formatPlaceholders(player, origin, destination, BaseUtil.formatColors(this.getGroupingObject(subTitleGrouping)));

        player.sendTitle(title, subTitle, 10, 70, 20);
    }

    @Override
    public CompletableFuture<Boolean> openCreate(ElevatorType elevator, Player player, byte direction) {
        return null;
    }
}
