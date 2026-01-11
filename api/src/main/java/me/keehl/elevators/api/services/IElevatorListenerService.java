package me.keehl.elevators.api.services;

import me.keehl.elevators.api.models.ElevatorEventExecutor;
import org.bukkit.event.*;

public interface IElevatorListenerService extends IElevatorService {

    <T extends Event> void registerEventExecutor(Class<T> clazz, EventPriority priority, ElevatorEventExecutor<T> executor, boolean ignoreCancelled);

    <T extends Event> void registerEventExecutor(Class<T> clazz, EventPriority priority, ElevatorEventExecutor<T> executor);
}
