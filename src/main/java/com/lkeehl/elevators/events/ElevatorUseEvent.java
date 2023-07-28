package com.lkeehl.elevators.events;

import com.lkeehl.elevators.models.ElevatorSearchResult;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ElevatorUseEvent extends Event implements Cancellable {

    private final Player player;
    private final ShulkerBox originShulkerBox;

    private final ElevatorSearchResult searchResult;

    private boolean cancelled = false;

    private static final HandlerList handlers = new HandlerList();

    public ElevatorUseEvent(Player player, ShulkerBox origin, ElevatorSearchResult searchResult) {
        super(false);

        this.player = player;
        this.originShulkerBox = origin;
        this.searchResult = searchResult;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ElevatorType getElevatorType() {
        return this.searchResult.getElevatorType();
    }

    public ShulkerBox getOriginShulkerBox() {
        return this.originShulkerBox;
    }

    public ShulkerBox getDestinationShulkerBox() {
        return this.searchResult.getDestination();
    }

    public byte getDirection() {
        return this.searchResult.getDirection();
    }

    public ElevatorSearchResult getSearchResult() {
        return this.searchResult;
    }

    public boolean isUp() {
        return this.searchResult.getDirection() == 1;
    }

    public boolean isDown() {
        return this.searchResult.getDirection() == -1;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

}
