package me.keehl.elevators.events;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.models.ElevatorType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ElevatorUseEvent extends Event implements Cancellable {

    private final Player player;

    private final IElevatorEventData searchResult;

    private boolean cancelled = false;

    private static final HandlerList handlers = new HandlerList();

    public ElevatorUseEvent(Player player, IElevatorEventData searchResult) {
        super(false);

        this.player = player;
        this.searchResult = searchResult;
    }

    public Player getPlayer() {
        return this.player;
    }

    public IElevatorType getElevatorType() {
        return this.searchResult.getOrigin().getElevatorType();
    }


    public byte getDirection() {
        return this.searchResult.getDirection();
    }

    public IElevatorEventData getSearchResult() {
        return this.searchResult;
    }

    public IElevator getOriginElevator() {
        return this.searchResult.getOrigin();
    }

    public IElevator getDestinationElevator() {
        return this.searchResult.getDestination();
    }

    public boolean isUp() {
        return this.searchResult.getDirection() == 1;
    }

    public boolean isDown() {
        return this.searchResult.getDirection() == -1;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
