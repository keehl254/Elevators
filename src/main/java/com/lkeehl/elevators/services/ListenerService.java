package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.ElevatorEventExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.EventExecutor;

public class ListenerService {

    private static boolean initialized = false;

    private static Listener listener;

    public static void init() {
        if(ListenerService.initialized)
            return;

        // This might be over-engineered. I may back-track this.

        ListenerService.initialized = true;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T extends Event> void registerEventExecutor(Class<T> clazz, EventPriority priority, ElevatorEventExecutor<T> executor, boolean ignoreCancelled) {
        Bukkit.getPluginManager().registerEvent(clazz, listener, priority, (listener, event) -> executor.execute((T) event), Elevators.getInstance(), ignoreCancelled);
    }

    public static <T extends Event> void registerEventExecutor(Class<T> clazz, EventPriority priority, ElevatorEventExecutor<T> executor) {
        registerEventExecutor(clazz, priority, executor, false);
    }


}
