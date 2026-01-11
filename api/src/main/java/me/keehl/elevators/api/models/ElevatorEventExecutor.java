package me.keehl.elevators.api.models;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.jetbrains.annotations.NotNull;

public interface ElevatorEventExecutor<T extends Event> {

    void execute(@NotNull T event) throws EventException;

}
