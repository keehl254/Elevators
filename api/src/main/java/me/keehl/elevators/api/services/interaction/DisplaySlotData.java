package me.keehl.elevators.api.services.interaction;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

public class DisplaySlotData {

        private final int slot;
        private final ItemStack itemStack;
        private final DisplayClickFlag[] flags;
        private final BiFunction<InventoryClickEvent, ISimpleDisplay, DisplayClickResult> onClick;

        public DisplaySlotData(int slot, ItemStack itemStack, DisplayClickFlag[] flags, BiFunction<InventoryClickEvent, ISimpleDisplay, DisplayClickResult> onClick) {
            this.slot = slot;
            this.itemStack = itemStack;
            this.flags = flags;
            this.onClick = onClick;
        }

        public DisplayClickResult click(InventoryClickEvent event, ISimpleDisplay display) {
            return this.onClick.apply(event, display);
        }

    public DisplayClickFlag[] getFlags() {
        return this.flags;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public int getSlot() {
        return this.slot;
    }
}