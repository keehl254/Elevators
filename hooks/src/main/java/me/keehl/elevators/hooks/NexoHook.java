package me.keehl.elevators.hooks;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.hooks.ItemsHook;
import me.keehl.elevators.api.services.IElevatorRecipeService;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class NexoHook implements ItemsHook {

    @Override
    public void onInit() {

        IElevatorRecipeService recipeService = Bukkit.getServicesManager().load(IElevatorRecipeService.class);
        if(recipeService == null) {
            ElevatorsAPI.log(Level.WARNING, "Elevator Services not been setup yet. Nexo hook may not function.");
            return;
        }

        ElevatorsAPI.log("Nexo has been hooked. Reloading recipes for Nexo support");
        ElevatorsAPI.pushLog();
        recipeService.refreshRecipes();
        ElevatorsAPI.popLog();
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
