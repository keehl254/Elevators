package me.keehl.elevators.events;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class ElevatorMenuOpenEvent extends Event implements Cancellable {

    private final Player player;
    private final PlayerInteractEvent interactEvent;
    private final Elevator elevator;

    private boolean cancelled = false;

    private static final HandlerList handlers = new HandlerList();

    public ElevatorMenuOpenEvent(Player player, PlayerInteractEvent interactEvent, Elevator elevator) {
        super(false);

        this.player = player;
        this.interactEvent = interactEvent;
        this.elevator = elevator;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerInteractEvent getInteractEvent() {
        return this.interactEvent;
    }

    public Elevator getElevator() {
        return this.elevator;
    }

    public ElevatorType getElevatorType() {
        return this.elevator.getElevatorType();
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
