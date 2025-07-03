package me.keehl.elevators.services.hooks;

import me.keehl.elevators.models.hooks.ItemsHook;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderHook extends ItemsHook {

    @Override
    public ItemStack createItemStackFromKey(NamespacedKey key) {

        return null;
    }

    @Override
    public NamespacedKey getKeyFromItemStack(ItemStack item) {
        return null;
    }

}
