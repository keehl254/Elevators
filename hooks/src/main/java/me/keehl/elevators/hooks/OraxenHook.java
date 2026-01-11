package me.keehl.elevators.hooks;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.hooks.ItemsHook;
import me.keehl.elevators.api.services.IElevatorRecipeService;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.logging.Level;

public class OraxenHook implements ItemsHook {

    @Override
    public void onInit() {

        IElevatorRecipeService recipeService = Bukkit.getServicesManager().load(IElevatorRecipeService.class);
        if(recipeService == null) {
            ElevatorsAPI.log(Level.WARNING, "Elevator Services not been setup yet. Oraxen hook may not function.");
            return;
        }

        ElevatorsAPI.log("Oraxen has been hooked. Reloading recipes for Oraxen support");
        ElevatorsAPI.pushLog();
        recipeService.refreshRecipes();
        ElevatorsAPI.popLog();
    }

    @Override
    public ItemStack createItemStackFromKey(NamespacedKey key) {
        if(!key.getKey().equalsIgnoreCase("oraxen"))
            return null;

        Optional<ItemBuilder> itemBuilder = OraxenItems.getOptionalItemById(key.getNamespace());
        return itemBuilder.map(ItemBuilder::build).orElse(null);
    }

    @Override
    public NamespacedKey getKeyFromItemStack(ItemStack item) {
        String key = OraxenItems.getIdByItem(item);
        if(key == null)
            return null;

        return new NamespacedKey(key, "oraxen");
    }

}
