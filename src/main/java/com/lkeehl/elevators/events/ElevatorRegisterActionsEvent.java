package com.lkeehl.elevators.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ElevatorRegisterActionsEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public ElevatorRegisterActionsEvent() {
        super(false);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
