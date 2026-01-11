package me.keehl.elevators.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ElevatorRegisterSettingsEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public ElevatorRegisterSettingsEvent() {
        super(false);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
