package me.keehl.elevators.hooks;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.hooks.ItemsHook;
import me.keehl.elevators.services.ElevatorRecipeService;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class NexoHook extends ItemsHook {

    @Override
    public void onInit() {
        Elevators.log("Nexo has been hooked. Reloading recipes for Nexo support");
        Elevators.pushLog();
        ElevatorRecipeService.refreshRecipes();
        Elevators.popLog();
    }

    @Override
    public ItemStack createItemStackFromKey(NamespacedKey key) {
        if(!key.getKey().equalsIgnoreCase("nexo"))
            return null;

        ItemBuilder itemBuilder = NexoItems.itemFromId(key.getNamespace());
        if(itemBuilder == null)
            return null;

        return itemBuilder.getFinalItemStack();
    }

    @Override
    public NamespacedKey getKeyFromItemStack(ItemStack item) {
        String key = NexoItems.idFromItem(item);
        if(key == null)
            return null;

        return new NamespacedKey(key, "nexo");
    }

}
