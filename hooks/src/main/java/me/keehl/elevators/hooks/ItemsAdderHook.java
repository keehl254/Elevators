package me.keehl.elevators.hooks;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.hooks.ItemsHook;
import me.keehl.elevators.api.services.IElevatorListenerService;
import me.keehl.elevators.api.services.IElevatorRecipeService;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class ItemsAdderHook implements ItemsHook {

    @Override
    public void onInit() {
        // ItemsAdder.areItemsLoaded is not correct. Best to just assume it's not loaded; ItemsAdded will always fire an event when all plugins finish.
        ElevatorsAPI.log("ItemsAdder has been hooked, however has not finished loading yet. Waiting for ItemsAdder Data Load.");

        IElevatorListenerService listenerService = Bukkit.getServicesManager().load(IElevatorListenerService.class);
        IElevatorRecipeService recipeService = Bukkit.getServicesManager().load(IElevatorRecipeService.class);
        if(listenerService == null || recipeService == null) {
            ElevatorsAPI.log(Level.WARNING, "Elevator Services not been setup yet. ItemsAdder hook may not function.");
            return;
        }

        listenerService.registerEventExecutor(ItemsAdderLoadDataEvent.class, EventPriority.MONITOR, (ItemsAdderLoadDataEvent event) -> {
            ElevatorsAPI.log("Items Adder has finished loading. Reloading recipes for Items Adder support");
            ElevatorsAPI.pushLog();
            recipeService.refreshRecipes();
            ElevatorsAPI.popLog();
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
