package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.services.IElevatorListenerService;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.api.models.ElevatorEventExecutor;
import me.keehl.elevators.listeners.EntityEventExecutor;
import me.keehl.elevators.listeners.InventoryEventExecutor;
import me.keehl.elevators.listeners.PaperEventExecutor;
import me.keehl.elevators.listeners.WorldEventExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class ElevatorListenerService extends ElevatorService implements IElevatorListenerService {

    private boolean initialized = false;

    private final Listener listener = new Listener() {};

    public ElevatorListenerService(IElevators elevators) {
        super(elevators);
    }

    public void onInitialize() {
        if(this.initialized)
            return;
        ElevatorsAPI.pushAndHoldLog();

        // This might be over-engineered. I may back-track this.

        registerEventExecutor(InventoryOpenEvent.class, EventPriority.LOWEST , InventoryEventExecutor::onInventoryOpen);
        registerEventExecutor(InventoryClickEvent.class, EventPriority.HIGHEST, InventoryEventExecutor::onClickStackHandler, true);
        registerEventExecutor(InventoryClickEvent.class, EventPriority.LOWEST, InventoryEventExecutor::updateStackOnClick, true);
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
        registerEventExecutor(PlayerInteractEvent.class, EventPriority.HIGHEST, EntityEventExecutor::onRightClick);

        if(Elevators.getFoliaLib().isPaper()) {
            registerEventExecutor(com.destroystokyo.paper.event.player.PlayerJumpEvent.class, EventPriority.NORMAL, PaperEventExecutor::onJump, false);
            registerEventExecutor(InventoryMoveItemEvent.class, EventPriority.LOWEST , PaperEventExecutor::onHopperTake);
        }else {
            registerEventExecutor(PlayerMoveEvent.class, EventPriority.NORMAL, EntityEventExecutor::onJumpDefault, false);
            registerEventExecutor(InventoryMoveItemEvent.class, EventPriority.LOWEST , InventoryEventExecutor::onHopperTake);
        }

        TryRegisterCMIBackpackOpenEvent(this.listener);


        this.initialized = true;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Listener service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public void onUninitialize() {
        HandlerList.unregisterAll(this.listener);
        this.initialized = false;
    }

    /* I am not a fan of CMI since it is so massive with abhorrent API support.
    If it weren't for the fact the plugin lets players open elevators, I wouldn't want to support it. */
    private void TryRegisterCMIBackpackOpenEvent(Listener listener) {
        try {
            @SuppressWarnings("unchecked") Class<? extends Event> backpackOpenEventClass = (Class<? extends Event>) Class.forName("com.Zrips.CMI.events.CMIBackpackOpenEvent");
            Method getShulkerBoxMethod = backpackOpenEventClass.getMethod("getShulkerBox");
            getShulkerBoxMethod.setAccessible(true);

            Bukkit.getPluginManager().registerEvent(backpackOpenEventClass, listener, EventPriority.NORMAL, (passedlistener, event) -> {
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

    @SuppressWarnings({"unchecked"})
    public <T extends Event> void registerEventExecutor(Class<T> clazz, EventPriority priority, ElevatorEventExecutor<T> executor, boolean ignoreCancelled) {
        Bukkit.getPluginManager().registerEvent(clazz, this.listener, priority, (listener, event) ->
        {
            if(clazz.isAssignableFrom(event.getClass()))
                executor.execute((T) event);
        }, Elevators.getInstance(), ignoreCancelled);
    }

    public <T extends Event> void registerEventExecutor(Class<T> clazz, EventPriority priority, ElevatorEventExecutor<T> executor) {
        registerEventExecutor(clazz, priority, executor, false);
    }


}
