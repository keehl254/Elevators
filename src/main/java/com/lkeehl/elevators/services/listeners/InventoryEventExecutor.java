package com.lkeehl.elevators.services.listeners;

import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ElevatorPermHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.ShulkerBoxHelper;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.DataContainerService;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryEventExecutor {

    public static void onInventoryOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if(!(holder instanceof ShulkerBox box)) return;
        if (!ElevatorHelper.isElevator(box)) return;

        event.setCancelled(true);
        event.getPlayer().closeInventory();

        ShulkerBoxHelper.playClose(box);
    }

    public static void onHopperTake(InventoryMoveItemEvent event) {
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;
        if (item.getType().equals(Material.COMMAND_BLOCK) && (meta.getDisplayName().equalsIgnoreCase("elevator")))
            event.setCancelled(true);
        InventoryHolder from = event.getSource().getHolder();
        InventoryHolder to = event.getDestination().getHolder();

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(event.getItem());
        if (elevatorType != null)
            event.setItem(elevatorType.updateItemDisplay(event.getItem()));

        if (((from instanceof ShulkerBox fromBox) && (ElevatorHelper.isElevator(fromBox))) || ((to instanceof ShulkerBox toBox) && (ElevatorHelper.isElevator(toBox))))
            event.setCancelled(true);
    }

    public static void onAnvilPrepare(PrepareAnvilEvent e) {
        AnvilInventory inventory = e.getInventory();
        ItemStack item = inventory.getItem(0);
        if (item == null)
            return;
        ElevatorType type = ElevatorHelper.getElevatorType(item);
        if (type == null)
            return;

        if (e.getResult() == null)
            return;

        ItemStack result = e.getResult().clone();
        if (result.getType().isAir() || ItemStackHelper.isNotShulkerBox(result.getType()))
            return;
        ItemStack newElevator = new ItemStack(result.getType(), result.getAmount());
        DataContainerService.dumpDataFromItemIntoItem(item, newElevator);

        ItemMeta meta = newElevator.getItemMeta();
        if (meta != null && result.getItemMeta() != null && result.getItemMeta().hasDisplayName())
            meta.setDisplayName(result.getItemMeta().getDisplayName());
        newElevator.setItemMeta(meta);
        e.setResult(newElevator);
    }

    public void onDyeCraft(CraftItemEvent e) {
        if(!(e.getRecipe() instanceof Keyed keyedRecipe))
            return;
        if(!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack result = e.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) return;
        if (ItemStackHelper.isNotShulkerBox(result.getType())) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(result);
        if (elevatorType == null) return;

        boolean isElevatorCraftingRecipe = keyedRecipe.getKey().getNamespace().equalsIgnoreCase("elevators");
        DyeColor dyeColor = ItemStackHelper.getDyeColorFromMaterial(e.getRecipe().getResult().getType());

        String locale = null;

        if (isElevatorCraftingRecipe) {
            if(!ElevatorPermHelper.canCraftElevatorType(elevatorType, player, (Recipe & Keyed) e.getRecipe(), dyeColor))
                locale = BaseElevators.locale.get("cantCreateMessage");
        } else if (!ElevatorPermHelper.canDyeElevatorType(elevatorType, player, dyeColor))
            locale = BaseElevators.locale.get("cantDyeMessage");

        if(locale != null) {
            e.setCancelled(true);
            BaseUtil.sendMessage(player, locale);
        }
    }

}
