package me.keehl.elevators.services.hooks;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import me.keehl.elevators.models.hooks.ItemsHook;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class OraxenHook extends ItemsHook {

    @Override
    public ItemStack createItemStackFromKey(NamespacedKey key) {
        if(!key.getKey().equalsIgnoreCase("Oraxen"))
            return null;

        Optional<ItemBuilder> itemBuilder = OraxenItems.getOptionalItemById(key.getNamespace());
        return itemBuilder.map(ItemBuilder::build).orElse(null);
    }

    @Override
    public NamespacedKey getKeyFromItemStack(ItemStack item) {
        String namespace = OraxenItems.getIdByItem(item);
        if(namespace == null)
            return null;

        return NamespacedKey.fromString(namespace, Bukkit.getPluginManager().getPlugin("Oraxen"));
    }

}
