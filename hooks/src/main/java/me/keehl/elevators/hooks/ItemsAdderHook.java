package me.keehl.elevators.hooks;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.hooks.ItemsHook;
import me.keehl.elevators.api.services.IElevatorListenerService;
import me.keehl.elevators.api.services.IElevatorRecipeService;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

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

        listenerService.registerEventExecutor(PrepareItemCraftEvent.class, EventPriority.HIGHEST, this::onPrePrepareCraft);
        listenerService.registerEventExecutor(PrepareItemCraftEvent.class, EventPriority.MONITOR, this::onPostPrepareCraft);
    }

    /*
        Okay, so... This is not great, but it is the best I have right now until ItemsAdder makes a change.
        ItemsAdder sets the result of any recipe that contains an ItemAdder item to empty except for if
        their two or three supported plugins say otherwise. It does it in MONITOR as well, which should not
        be used to alter anything. This is dumb, they should just check if the recipe choice is an ExactChoice
        post 1.20 and return. Boom, fixed.

        I hate decompiling code because it feels in poor spirit, but I figured out that ItemsAdder will return
        early if the inventory result is not the same as recipe result. Soooo, I just add an enchantment to
        elevator recipes and then remove it later.
     */
    public void onPrePrepareCraft(PrepareItemCraftEvent e) {
        if (!(e.getRecipe() instanceof Keyed keyedRecipe))
            return;
        boolean isElevatorCraftingRecipe = keyedRecipe.getKey().getNamespace().equalsIgnoreCase("elevators");
        if(!isElevatorCraftingRecipe)
            return;

        if (!(keyedRecipe instanceof ShapedRecipe shapedRecipe))
            return;

        if(e.getInventory().getResult() == null)
            return;

        if(!e.getInventory().getResult().equals(shapedRecipe.getResult()))
            return;

        ItemStack cloneItem = e.getInventory().getResult().clone();
        cloneItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
        e.getInventory().setResult(cloneItem);
    }

    public void onPostPrepareCraft(PrepareItemCraftEvent e) {
        if (!(e.getRecipe() instanceof Keyed keyedRecipe))
            return;
        boolean isElevatorCraftingRecipe = keyedRecipe.getKey().getNamespace().equalsIgnoreCase("elevators");
        if(!isElevatorCraftingRecipe)
            return;

        if (!(keyedRecipe instanceof ShapedRecipe shapedRecipe))
            return;

        if(e.getInventory().getResult() == null)
            return;

        if(!e.getInventory().getResult().getItemMeta().hasEnchant(Enchantment.KNOCKBACK))
            return;

        e.getInventory().setResult(shapedRecipe.getResult());
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
