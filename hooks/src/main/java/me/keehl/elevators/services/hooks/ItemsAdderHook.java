package me.keehl.elevators.services.hooks;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.keehl.elevators.models.hooks.ItemsHook;
import me.keehl.elevators.services.ElevatorListenerService;
import me.keehl.elevators.services.ElevatorRecipeService;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderHook extends ItemsHook {

    public ItemsAdderHook() {

        ElevatorListenerService.registerEventExecutor(ItemsAdderLoadDataEvent.class, EventPriority.MONITOR, (ItemsAdderLoadDataEvent event) -> {
            ElevatorRecipeService.refreshRecipes();
        });

    }

    @Override
    public ItemStack createItemStackFromKey(NamespacedKey key) {
        CustomStack stack = CustomStack.getInstance(key.toString());
        if(stack == null)
            return null;
        return stack.getItemStack();
    }

    @Override
    public NamespacedKey getKeyFromItemStack(ItemStack item) {
        CustomStack stack = CustomStack.byItemStack(item);
        if(stack == null)
            return null;

        return new NamespacedKey(stack.getNamespace(), stack.getId());
    }

}
