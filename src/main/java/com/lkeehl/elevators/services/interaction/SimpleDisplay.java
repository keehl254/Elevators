package com.lkeehl.elevators.services.interaction;

import com.lkeehl.elevators.Elevators;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class SimpleDisplay implements Listener {

    private final JavaPlugin plugin;
    private final Player player;
    private final Inventory inventory;
    private final DisplayClickResult defaultClickResult;
    private Runnable returnRunnable;

    private boolean blockReturn = false;

    private final Map<Integer, DisplaySlotData> slotDataMap = new HashMap<>();

    private final Map<String, Object> cache = new HashMap<>();

    private DisplayClickFlag[] initialFlags;
    private BiFunction<InventoryClickEvent, SimpleDisplay, DisplayClickResult> initialOnClick;

    public SimpleDisplay(JavaPlugin plugin, Player player, Inventory inventory) {
        this(plugin, player, inventory, null);
    }

    public SimpleDisplay(JavaPlugin plugin, Player player, Inventory inventory, Runnable returnRunnable) {
        this(plugin, player, inventory, returnRunnable, DisplayClickResult.CANCEL);
    }

    public SimpleDisplay(JavaPlugin plugin, Player player, Inventory inventory, Runnable returnRunnable, DisplayClickResult defaultClickResult) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = inventory;
        this.defaultClickResult = defaultClickResult;
        this.returnRunnable = returnRunnable;
    }

    public SimpleDisplay setItem(int index, ItemStack itemstack, BiFunction<InventoryClickEvent, SimpleDisplay, DisplayClickResult> onClick, DisplayClickFlag... flags) {
        slotDataMap.remove(index);
        slotDataMap.put(index, new DisplaySlotData(index, itemstack, flags, onClick));

        this.inventory.setItem(index, itemstack);
        return this;
    }

    public SimpleDisplay setItemSimple(int index, ItemStack itemstack, BiConsumer<InventoryClickEvent, SimpleDisplay> onClick, DisplayClickFlag... flags) {
        return setItem(index, itemstack, (event, myDisplay) -> {
            onClick.accept(event, myDisplay);
            return DisplayClickResult.DEFAULT;
        }, flags);
    }

    public SimpleDisplay setReturnButton(int index, ItemStack itemstack, DisplayClickFlag... flags) {
        return setItem(index, itemstack, (event, myDisplay) -> {
            myDisplay.returnOrClose();
            return DisplayClickResult.DEFAULT;
        }, flags);
    }

    public SimpleDisplay clearActions() {
        slotDataMap.clear();
        return this;
    }

    public void close(boolean executeReturn) {
        if(!executeReturn)
            HandlerList.unregisterAll(this);
        player.closeInventory();
    }

    public void open() {
        player.openInventory(inventory);
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    public void returnOrClose() {
        HandlerList.unregisterAll(this);
        if(this.returnRunnable != null && !this.blockReturn)
            this.returnRunnable.run();
        else
            this.player.closeInventory();
    }

    public void stopReturn() {
        this.blockReturn = true;
    }

    public SimpleDisplay onClick(BiFunction<InventoryClickEvent, SimpleDisplay, DisplayClickResult> onClick, DisplayClickFlag... initialFlags) {
        this.initialFlags = initialFlags;
        this.initialOnClick = onClick;

        return this;
    }

    public SimpleDisplay onReturn(Runnable onReturn) {
        this.returnRunnable = onReturn;
        return this;
    }

    private DisplayClickResult validateSlotClicks(InventoryClickEvent event) {

        if (event.getClickedInventory() != this.inventory)
            return DisplayClickResult.DEFAULT;

        int clickedSlot = event.getSlot();

        if (!slotDataMap.containsKey(clickedSlot))
            return DisplayClickResult.DEFAULT;

        DisplaySlotData slotData = this.slotDataMap.get(clickedSlot);
        if (!DisplayClickFlag.isValid(event, slotData.flags))
            return DisplayClickResult.DEFAULT;

        try {
            return slotData.onClick.apply(event, this);
        } catch (Exception ignore) {
            return this.defaultClickResult;
        }
    }

    private DisplayClickResult validateInitialClick(InventoryClickEvent event) {
        if (this.initialOnClick == null) return DisplayClickResult.DEFAULT;
        if (this.initialFlags != null && !DisplayClickFlag.isValid(event, this.initialFlags))
            return DisplayClickResult.DEFAULT;
        try {
            return this.initialOnClick.apply(event, this);
        } catch (Exception ignore) {
            return this.defaultClickResult;
        }
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != this.inventory) return;

        DisplayClickResult clickResult = DisplayClickResult.combineResults(this.defaultClickResult, this.validateSlotClicks(event), this.validateInitialClick(event));
        event.setCancelled(clickResult == DisplayClickResult.CANCEL);
    }

    @EventHandler()
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getPlayer() != this.player) return; // Regardless of the inventory, if the player is closing an inventory, this display should stop xD

        HandlerList.unregisterAll(this);

        if(this.returnRunnable == null) return;
        if(this.blockReturn) return;

        Elevators.getFoliaLib().getScheduler().runAtEntityLater(event.getPlayer(), this.returnRunnable, 1L);
    }

    public <T> T getOrDefaultCache(String key, T defaultT) {
        if(!this.cache.containsKey(key))
            return defaultT;
        return (T) this.cache.get(key);
    }

    public void setCache(String key, Object value) {
        this.cache.put(key, value);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public boolean hasReturn() {
        return this.returnRunnable != null;
    }

    public enum DisplayClickResult {
        CANCEL,
        ALLOW,
        DEFAULT;

        public static DisplayClickResult combineResults(DisplayClickResult defaultClickResult, DisplayClickResult... results) {
            Stream<DisplayClickResult> resultStream = Arrays.stream(results).filter(i -> i != DisplayClickResult.DEFAULT);
            return resultStream.filter(i -> i != defaultClickResult).findAny().orElse(defaultClickResult);
        }
    }

    public enum DisplayClickFlag {
        IN_MENU(e -> e.getClickedInventory() == e.getInventory()),
        LEFT_CLICK(InventoryClickEvent::isLeftClick),
        RIGHT_CLICK(InventoryClickEvent::isRightClick),
        SHIFT_CLICK(InventoryClickEvent::isShiftClick);

        private final Function<InventoryClickEvent, Boolean> validationFunction;

        DisplayClickFlag(Function<InventoryClickEvent, Boolean> validationFunction) {
            this.validationFunction = validationFunction;
        }

        public boolean isValid(InventoryClickEvent event) {
            return validationFunction.apply(event);
        }

        public static boolean isValid(InventoryClickEvent event, DisplayClickFlag... flags) {
            return Arrays.stream(flags).allMatch(i -> i.isValid(event));
        }
    }

    public record DisplaySlotData(int slot, ItemStack itemStack, DisplayClickFlag[] flags,
                                  BiFunction<InventoryClickEvent, SimpleDisplay, DisplayClickResult> onClick) {
    }

}
