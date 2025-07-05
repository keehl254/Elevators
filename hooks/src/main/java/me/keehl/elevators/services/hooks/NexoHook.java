package me.keehl.elevators.services.hooks;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.hooks.ItemsHook;
import me.keehl.elevators.services.ElevatorRecipeService;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class NexoHook extends ItemsHook {

    public NexoHook() {
        Elevators.log("Nexo has been hooked. Reloading recipes for Nexo support");
        Elevators.pushLog();
        ElevatorRecipeService.refreshRecipes();
        Elevators.popLog();
    }

    @Override
    public ItemStack createItemStackFromKey(NamespacedKey key) {
        if(!key.getKey().equalsIgnoreCase("Nexo"))
            return null;

        ItemBuilder itemBuilder = NexoItems.itemFromId(key.getKey());
        if(itemBuilder == null)
            return null;

        return itemBuilder.getFinalItemStack();
    }

    @Override
    public NamespacedKey getKeyFromItemStack(ItemStack item) {
        String key = NexoItems.idFromItem(item);
        if(key == null)
            return null;

        return new NamespacedKey("Nexo", key);
    }

}
