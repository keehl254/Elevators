package me.keehl.elevators.api.models.hooks;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface ItemsHook extends ElevatorHook {

    ItemStack createItemStackFromKey(NamespacedKey key);

    NamespacedKey getKeyFromItemStack(ItemStack item);

}
