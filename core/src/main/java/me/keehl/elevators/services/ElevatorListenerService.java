package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.models.ElevatorEventExecutor;
import me.keehl.elevators.services.listeners.EntityEventExecutor;
import me.keehl.elevators.services.listeners.InventoryEventExecutor;
import me.keehl.elevators.services.listeners.PaperEventExecutor;
import me.keehl.elevators.services.listeners.WorldEventExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ElevatorListenerService {

    private static boolean initialized = false;

    private static Listener listener;

    public static void init() {
        if(ElevatorListenerService.initialized)
            return;

        listener = new Listener() {};

        // This might be over-engineered. I may back-track this.

        registerEventExecutor(InventoryOpenEvent.class, EventPriority.LOWEST , InventoryEventExecutor::onInventoryOpen);
        registerEventExecutor(InventoryClickEvent.class, EventPriority.HIGHEST, InventoryEventExecutor::onClickStackHandler, true);
        registerEventExecutor(PrepareAnvilEvent.class, EventPriority.LOWEST , InventoryEventExecutor::onAnvilPrepare);
        registerEventExecutor(CraftItemEvent.class, EventPriority.NORMAL , InventoryEventExecutor::onCraft);

        registerEventExecutor(BlockPistonExtendEvent.class, EventPriority.NORMAL , WorldEventExecutor::onPistonExtend);
        registerEventExecutor(EntityExplodeEvent.class, EventPriority.NORMAL , WorldEventExecutor::onExplode);
        registerEventExecutor(BlockDispenseEvent.class, EventPriority.NORMAL , WorldEventExecutor::onDispenserPlace);
        registerEventExecutor(BlockDropItemEvent.class, EventPriority.LOWEST , WorldEventExecutor::onBlockBreak);
        registerEventExecutor(BlockPlaceEvent.class, EventPriority.HIGHEST , WorldEventExecutor::onBlockPlace);
        registerEventExecutor(ChunkLoadEvent.class, EventPriority.MONITOR, WorldEventExecutor::onChunkLoad);
        registerEventExecutor(ChunkUnloadEvent.class, EventPriority.HIGHEST, WorldEventExecutor::onChunkUnload);

        registerEventExecutor(PlayerJoinEvent.class, EventPriority.NORMAL, EntityEventExecutor::onJoin);
        registerEventExecutor(PlayerToggleSneakEvent.class, EventPriority.NORMAL , EntityEventExecutor::onSneak);
        registerEventExecutor(EntityPickupItemEvent.class, EventPriority.NORMAL , EntityEventExecutor::onPickup);
        registerEventExecutor(PlayerInteractEvent.class, EventPriority.NORMAL, EntityEventExecutor::onRightClick);

        if(Elevators.getFoliaLib().isPaper()) {
            registerEventExecutor(com.destroystokyo.paper.event.player.PlayerJumpEvent.class, EventPriority.NORMAL, PaperEventExecutor::onJump, false);
            registerEventExecutor(InventoryMoveItemEvent.class, EventPriority.LOWEST , PaperEventExecutor::onHopperTake);
        }else {
            registerEventExecutor(PlayerMoveEvent.class, EventPriority.NORMAL, EntityEventExecutor::onJumpDefault, false);
            registerEventExecutor(InventoryMoveItemEvent.class, EventPriority.LOWEST , InventoryEventExecutor::onHopperTake);
        }

        /* I am not a fan of CMI since it is so massive with abhorrent API support.
        If it weren't for the fact the plugin lets players open elevators, I wouldn't want to support it. */
        if (Bukkit.getPluginManager().isPluginEnabled("CMI")) {

            try {
                @SuppressWarnings("unchecked") Class<? extends Event> backpackOpenEventClass = (Class<? extends Event>) Class.forName("com.Zrips.CMI.events.CMIBackpackOpenEvent");
                Method getShulkerBoxMethod = backpackOpenEventClass.getMethod("getShulkerBox");
                getShulkerBoxMethod.setAccessible(true);

                Bukkit.getPluginManager().registerEvent(backpackOpenEventClass, listener, EventPriority.NORMAL, (listener, event) -> {
                    if(event.getClass() != backpackOpenEventClass)
                        return;
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

        ElevatorListenerService.initialized = true;
    }

    public static void unInitialize() {
        if(ElevatorListenerService.listener != null)
            HandlerList.unregisterAll(ElevatorListenerService.listener);

        ElevatorListenerService.initialized = false;
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
