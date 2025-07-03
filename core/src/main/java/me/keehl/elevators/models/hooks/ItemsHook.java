package me.keehl.elevators.models.hooks;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class ItemsHook implements ElevatorHook {

    public abstract ItemStack createItemStackFromKey(NamespacedKey key);

    public abstract NamespacedKey getKeyFromItemStack(ItemStack item);

}
