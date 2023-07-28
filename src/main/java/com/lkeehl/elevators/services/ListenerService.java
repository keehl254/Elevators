package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.ShulkerBoxHelper;
import com.lkeehl.elevators.models.ElevatorEventExecutor;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.listeners.InventoryEventExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ListenerService {

    private static boolean initialized = false;

    private static Listener listener;

    public static void init() {
        if(ListenerService.initialized)
            return;

        // This might be over-engineered. I may back-track this.

        registerEventExecutor(InventoryOpenEvent.class, EventPriority.LOWEST , InventoryEventExecutor::onInventoryOpen, false);

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
