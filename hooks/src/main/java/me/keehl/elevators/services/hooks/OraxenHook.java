package me.keehl.elevators.services.hooks;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.hooks.ItemsHook;
import me.keehl.elevators.services.ElevatorRecipeService;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class OraxenHook extends ItemsHook {

    @Override
    public void onInit() {
        Elevators.log("Oraxen has been hooked. Reloading recipes for Oraxen support");
        Elevators.pushLog();
        ElevatorRecipeService.refreshRecipes();
        Elevators.popLog();
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
