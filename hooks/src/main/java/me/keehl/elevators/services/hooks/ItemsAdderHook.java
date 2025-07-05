package me.keehl.elevators.services.hooks;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.hooks.ItemsHook;
import me.keehl.elevators.services.ElevatorListenerService;
import me.keehl.elevators.services.ElevatorRecipeService;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderHook extends ItemsHook {

    public ItemsAdderHook() {

        // ItemsAdder.areItemsLoaded is not correct. Best to just assume it's not loaded; ItemsAdded will always fire an event when all plugins finish.
        Elevators.log("ItemsAdder has been hooked, however has not finished loading yet. Waiting for ItemsAdder Data Load.");

        ElevatorListenerService.registerEventExecutor(ItemsAdderLoadDataEvent.class, EventPriority.MONITOR, (ItemsAdderLoadDataEvent event) -> {
            Elevators.log("Items Adder has finished loading. Reloading recipes for Items Adder support");
            Elevators.pushLog();
            ElevatorRecipeService.refreshRecipes();
            Elevators.popLog();
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
