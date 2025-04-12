package com.lkeehl.elevators.services.listeners;

import com.lkeehl.elevators.helpers.*;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.settings.DisplayNameSetting;
import com.lkeehl.elevators.models.settings.LoreLinesSetting;
import com.lkeehl.elevators.models.settings.MaxStackSizeSetting;
import com.lkeehl.elevators.services.DataContainerService;
import com.lkeehl.elevators.services.ElevatorSettingService;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryEventExecutor {

    public static void onInventoryOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof ShulkerBox box)) return;
        if (!ElevatorHelper.isElevator(box)) return;

        event.setCancelled(true);
        event.getPlayer().closeInventory();

        ShulkerBoxHelper.playClose(box);
    }

    @SuppressWarnings("ConstantConditions")
    public static void onClickStackHandler(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clickedItem = event.getCurrentItem();

        if (event.getCursor() == null || event.getCursor().getType().isAir()) { // Only for halving elevators to cursor.
            if (event.getClick() != ClickType.RIGHT) return;

            if (clickedItem == null) return;
            if (ItemStackHelper.isNotShulkerBox(clickedItem.getType())) return;
            if (!ElevatorHelper.isElevator(clickedItem)) return;

            event.setCancelled(true);

            int currentLeftSize = (clickedItem.getAmount() - (clickedItem.getAmount() % 2)) / 2; // Change size of the current item rather than cursor to mimic MC behavior.

            ItemStack cursorItem = clickedItem.clone();
            cursorItem.setAmount(cursorItem.getAmount() - currentLeftSize);

            clickedItem.setAmount(currentLeftSize);
            player.setItemOnCursor(cursorItem);
            return;
        }
        if (event.getClick() != ClickType.LEFT) return; // Dropping outside still counts.
        if (event.getClickedInventory() == null) return; // In case they drop it outside their inventory.
        if (ItemStackHelper.isNotShulkerBox(event.getCursor().getType())) return;
        if (!ElevatorHelper.isElevator(event.getCursor())) return;


        if (clickedItem == null) {
            event.setCancelled(true);
            return;
        }

        // Adding cursor to clicked stack.
        if (!clickedItem.getType().isAir() && clickedItem instanceof ShulkerBox) {
            ElevatorType elevatorType = ElevatorHelper.getElevatorType(clickedItem);
            int amountToAdd = ElevatorSettingService.getSettingValue(elevatorType, MaxStackSizeSetting.class) - clickedItem.getAmount();
            amountToAdd = Math.min(amountToAdd, event.getCursor().getAmount());

            clickedItem.setAmount(clickedItem.getAmount() + amountToAdd);
            event.getCursor().setAmount(event.getCursor().getAmount() - amountToAdd);
        }
    }

    @SuppressWarnings("deprecation")
    public static void onHopperTake(InventoryMoveItemEvent event) {
        if (event.getSource().getType() != InventoryType.SHULKER_BOX && event.getDestination().getType() != InventoryType.SHULKER_BOX)
            return;
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (item.getType().equals(Material.COMMAND_BLOCK) && (meta.getDisplayName().equalsIgnoreCase("elevator"))) {
            event.setCancelled(true);
            return;
        }

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(event.getItem());
        if (elevatorType != null) {
            meta.setDisplayName(MessageHelper.formatColors(ElevatorSettingService.getSettingValue(elevatorType, DisplayNameSetting.class)));
            meta.setLore(MessageHelper.formatColors(ElevatorSettingService.getSettingValue(elevatorType, LoreLinesSetting.class)));
            event.getItem().setItemMeta(meta);
        }
        if (event.getSource().getType() == InventoryType.SHULKER_BOX && event.getSource().getHolder() instanceof ShulkerBox fromBox) {
            if (ElevatorHelper.isElevator(fromBox))
                event.setCancelled(true);
        }
        if (event.getDestination().getType() == InventoryType.SHULKER_BOX && event.getDestination().getHolder() instanceof ShulkerBox toBox) {
            if (ElevatorHelper.isElevator(toBox))
                event.setCancelled(true);
        }
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

    public static void onCraft(CraftItemEvent e) {
        if (!(e.getRecipe() instanceof Keyed keyedRecipe)) return;
        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack result = e.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) return;
        if (ItemStackHelper.isNotShulkerBox(result.getType())) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(result);
        if (elevatorType == null) return;

        boolean isElevatorCraftingRecipe = keyedRecipe.getKey().getNamespace().equalsIgnoreCase("elevators");
        DyeColor dyeColor = ItemStackHelper.getDyeColorFromMaterial(e.getInventory().getResult().getType());

        if (isElevatorCraftingRecipe) {

            // There is no need to pass in the color as the recipe can only represent one color anyway.
            if (!ElevatorPermHelper.canCraftElevatorType(elevatorType, player, (Recipe & Keyed) e.getRecipe())) {
                MessageHelper.sendCantCreateMessage(player, new ElevatorEventData(elevatorType));
                e.setCancelled(true);
            }
        } else if (!ElevatorPermHelper.canDyeElevatorType(elevatorType, player, dyeColor)) {
            MessageHelper.sendCantDyeMessage(player, new ElevatorEventData(elevatorType));
            e.setCancelled(true);
        }
    }

}
