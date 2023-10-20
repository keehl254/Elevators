package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.models.ElevatorEventExecutor;
import com.lkeehl.elevators.services.listeners.EntityEventExecutor;
import com.lkeehl.elevators.services.listeners.InventoryEventExecutor;
import com.lkeehl.elevators.services.listeners.PaperEventExecutor;
import com.lkeehl.elevators.services.listeners.WorldEventExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ListenerService {

    private static boolean initialized = false;

    private static Listener listener;

    public static void init() {
        if(ListenerService.initialized)
            return;

        listener = new Listener() {};

        // This might be over-engineered. I may back-track this.

        registerEventExecutor(InventoryOpenEvent.class, EventPriority.LOWEST , InventoryEventExecutor::onInventoryOpen, false);
        registerEventExecutor(InventoryMoveItemEvent.class, EventPriority.LOWEST , InventoryEventExecutor::onHopperTake, false);
        registerEventExecutor(PrepareAnvilEvent.class, EventPriority.LOWEST , InventoryEventExecutor::onAnvilPrepare, false);
        registerEventExecutor(CraftItemEvent.class, EventPriority.NORMAL , InventoryEventExecutor::onDyeCraft, false);

        registerEventExecutor(BlockPistonExtendEvent.class, EventPriority.NORMAL , WorldEventExecutor::onPistonExtend, false);
        registerEventExecutor(EntityExplodeEvent.class, EventPriority.NORMAL , WorldEventExecutor::onExplode, false);
        registerEventExecutor(PlayerInteractEvent.class, EventPriority.NORMAL , WorldEventExecutor::onRightClick, false);
        registerEventExecutor(BlockDispenseEvent.class, EventPriority.NORMAL , WorldEventExecutor::onDispenserPlace, false);
        registerEventExecutor(BlockDropItemEvent.class, EventPriority.LOWEST , WorldEventExecutor::onBlockBreak, false);
        registerEventExecutor(BlockPlaceEvent.class, EventPriority.NORMAL , WorldEventExecutor::onBlockPlace, false);

        registerEventExecutor(PlayerToggleSneakEvent.class, EventPriority.NORMAL , EntityEventExecutor::onSneak, false);

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            registerEventExecutor(com.destroystokyo.paper.event.player.PlayerJumpEvent.class, EventPriority.NORMAL , PaperEventExecutor::onJump, false);
        } catch (ClassNotFoundException ignored) {
            registerEventExecutor(PlayerMoveEvent.class, EventPriority.NORMAL , EntityEventExecutor::onJumpDefault, false);
        }

        /* I hate CMI. This plugin is way too massive with abhorrent API support.
        If it weren't for the fact the plugin let people open elevators, I would never work with it. */
        if (Bukkit.getPluginManager().isPluginEnabled("CMI")) {
            try {
                @SuppressWarnings("unchecked") Class<? extends Event> backpackOpenEventClass = (Class<? extends Event>) Class.forName("com.Zrips.CMI.events.CMIBackpackOpenEvent");
                Method getShulkerBoxMethod = backpackOpenEventClass.getMethod("getShulkerBox");
                getShulkerBoxMethod.setAccessible(true);

                Bukkit.getPluginManager().registerEvent(backpackOpenEventClass, listener, EventPriority.NORMAL, (listener, event) -> {
                    try {
                        ItemStack item = (ItemStack) getShulkerBoxMethod.invoke(event);
                        if(ElevatorHelper.isElevator(item))
                            ((Cancellable) event).setCancelled(true);
                    } catch (IllegalAccessException | InvocationTargetException ignored) {
                    }
                }, Elevators.getInstance(), false);

            } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            }
        }

        ListenerService.initialized = true;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Event> void registerEventExecutor(Class<T> clazz, EventPriority priority, ElevatorEventExecutor<T> executor, boolean ignoreCancelled) {
        Bukkit.getPluginManager().registerEvent(clazz, listener, priority, (listener, event) ->
        {
            if(clazz.isAssignableFrom(event.getClass()))
                executor.execute((T) event);
        }, Elevators.getInstance(), ignoreCancelled);
    }

    public static <T extends Event> void registerEventExecutor(Class<T> clazz, EventPriority priority, ElevatorEventExecutor<T> executor) {
        registerEventExecutor(clazz, priority, executor, false);
    }


}
