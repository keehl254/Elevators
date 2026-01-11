package me.keehl.elevators.api.services.interaction;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface ISimpleDisplay {

    ISimpleDisplay setItem(int index, ItemStack itemstack, BiFunction<InventoryClickEvent, ISimpleDisplay, DisplayClickResult> onClick, DisplayClickFlag... flags);

    ISimpleDisplay setItemSimple(int index, ItemStack itemstack, BiConsumer<InventoryClickEvent, ISimpleDisplay> onClick, DisplayClickFlag... flags);

    ISimpleDisplay setReturnButton(int index, ItemStack itemstack, DisplayClickFlag... flags);

    ISimpleDisplay clearActions();

    void close(boolean executeReturn);

    void open();

    void returnOrClose();

    void stopReturn();

    ISimpleDisplay onClick(BiFunction<InventoryClickEvent, ISimpleDisplay, DisplayClickResult> onClick, DisplayClickFlag... initialFlags);

    ISimpleDisplay onReturn(Runnable onReturn);

    <T> T getOrDefaultCache(String key, T defaultT);

    void setCache(String key, Object value);

    Inventory getInventory();

    boolean hasReturn();

}
