package me.keehl.elevators.services.hooks;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import me.keehl.elevators.models.hooks.ItemsHook;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class NexoHook extends ItemsHook {

    @Override
    public ItemStack createItemStackFromKey(NamespacedKey key) {
        if(!key.getKey().equalsIgnoreCase("Nexo"))
            return null;

        ItemBuilder itemBuilder = NexoItems.itemFromId(key.getNamespace());
        if(itemBuilder == null)
            return null;

        return itemBuilder.getFinalItemStack();
    }

    @Override
    public NamespacedKey getKeyFromItemStack(ItemStack item) {
        String namespace = NexoItems.idFromItem(item);
        if(namespace == null)
            return null;

        return NamespacedKey.fromString(namespace, Bukkit.getPluginManager().getPlugin("Nexo"));
    }

}
