package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.DataContainerService;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Pattern;

public class ItemStackHelper {

    public static boolean isNotShulkerBox(Material type) {
        return !type.toString().endsWith("SHULKER_BOX");
    }

    public static DyeColor getDyeColorFromMaterial(Material material) {
        String colorString = material.name().split("_")[0];
        try {
            return DyeColor.valueOf(colorString);
        } catch (Exception ignored) {
        }

        return null;
    }

    public static Material getVariant(Material type, DyeColor color) {
        String name = type.toString().toLowerCase();

        for (DyeColor tColor : DyeColor.values()) {
            if (name.startsWith(tColor.toString().toLowerCase() + "_"))
                name = name.replaceFirst(Pattern.quote(tColor.toString().toLowerCase()), "");
        }
        name = (color.toString() + name).toUpperCase();
        Material variant = Material.matchMaterial(name);
        return variant == null ? type : variant;
    }

    private static ItemStack findElevatorType(ElevatorType elevatorType, ItemStack item, Inventory inv) {
        ItemStack elevator = null;
        for (ItemStack content : inv.getContents()) {
            if (content == null || content.getType().equals(Material.AIR))
                continue;
            if (ElevatorHelper.getElevatorType(content) != null && content.isSimilar(item)) {
                if (!Objects.equals(ElevatorHelper.getElevatorType(content), elevatorType))
                    continue;
                if (content.getAmount() >= elevatorType.getMaxStackSize())
                    continue;
                elevator = content;
                break;
            }
        }
        return elevator;
    }

    public static ItemStack createItemStackFromElevatorType(ElevatorType elevatorType, DyeColor dyeColor) {
        ItemStack itemStack = new ItemStack(getVariant(Material.WHITE_SHULKER_BOX, dyeColor), 1);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack; // How?

        meta.setDisplayName(MessageHelper.formatColors(elevatorType.getDisplayName()));
        meta.setLore(MessageHelper.formatColors(elevatorType.getLore()));

        itemStack.setItemMeta(meta);

        DataContainerService.setElevatorKey(itemStack, elevatorType);

        return itemStack;
    }

    public static ItemStack createItemStackFromElevator(Elevator elevator) {

        ItemStack itemStack = createItemStackFromElevatorType(elevator.getElevatorType(), DyeColor.WHITE);
        itemStack.setType(elevator.getShulkerBox().getType());

        DataContainerService.dumpDataFromShulkerBoxIntoItem(elevator.getShulkerBox(), itemStack);

        return itemStack;
    }

    public static void giveElevator(Item itemEntity, Inventory inv) {
        ItemStack item = itemEntity.getItemStack();
        ElevatorType elevatorType = ElevatorHelper.getElevatorType(item);
        if (elevatorType == null)
            return;
        DataContainerService.updateItemStackFromV2(item, elevatorType);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageHelper.formatColors(elevatorType.getDisplayName()));
            meta.setLore(MessageHelper.formatColors(elevatorType.getLore()));
            item.setItemMeta(meta);
        }

        while (item.getAmount() > 0) {
            ItemStack elevator = findElevatorType(elevatorType, item, inv);
            if (elevator == null) {
                elevator = item.clone();
                elevator.setAmount(1);
                if (!inv.addItem(elevator).isEmpty())
                    break;
            } else
                elevator.setAmount(elevator.getAmount() + 1);
            item.setAmount(item.getAmount() - 1);
        }
        if (item.getAmount() <= 0)
            itemEntity.remove();
        else
            itemEntity.setItemStack(item);
    }

    public static Map<ItemStack, Integer> addElevatorToInventory(ElevatorType elevatorType, int itemAmount, Material dyeMaterial, Inventory inventory) {
        return ItemStackHelper.addElevatorToInventory(elevatorType, itemAmount, dyeMaterial, inventory, elevatorType.getDisplayName(), elevatorType.getLore());
    }

    public static Map<ItemStack, Integer> addElevatorToInventory(ElevatorType elevatorType, int itemAmount, Material dyeMaterial, Inventory inventory, String displayName, List<String> lore) {
        displayName = MessageHelper.formatColors(displayName);
        lore = MessageHelper.formatColors(lore);

        Map<ItemStack, Integer> partialList = new HashMap<>();
        ItemStack newElevator = ItemStackHelper.createItemStackFromElevatorType(elevatorType, getDyeColorFromMaterial(dyeMaterial));

        for(ItemStack inventoryItem : inventory.getContents()) {
            if(inventoryItem == null) continue;
            if(inventoryItem.getType() != dyeMaterial) continue;
            if(ItemStackHelper.isNotShulkerBox(inventoryItem.getType())) continue; // This should technically be caught on the last line.
            if(!ElevatorHelper.isElevator(inventoryItem)) continue;
            if(ElevatorHelper.getElevatorType(inventoryItem) != elevatorType) continue;

            if(inventoryItem.getAmount() >= elevatorType.getMaxStackSize()) continue; // Dud .-.

            // Found our first partial :)

            if(!inventoryItem.isSimilar(newElevator)) continue;

            int amountToGive = elevatorType.getMaxStackSize() - inventoryItem.getAmount();
            amountToGive = Math.min(amountToGive, itemAmount);

            itemAmount -= amountToGive;
            inventoryItem.setAmount(inventoryItem.getAmount() + amountToGive);

            if(itemAmount == 0)
                break;
        }

        if(itemAmount > 0) {
            ItemMeta itemMeta = newElevator.getItemMeta();
            itemMeta.setDisplayName(displayName);
            itemMeta.setLore(lore);
            newElevator.setItemMeta(itemMeta);

            int leftoverToGive = itemAmount % elevatorType.getMaxStackSize();
            int stacksToGive = (itemAmount - leftoverToGive) / elevatorType.getMaxStackSize();
            for(int i=0; i < stacksToGive; i++) {
                ItemStack newItem = newElevator.clone();
                newItem.setAmount(elevatorType.getMaxStackSize());

                partialList.put(newItem, elevatorType.getMaxStackSize());
            }

            ItemStack newItem = newElevator.clone();
            newItem.setAmount(leftoverToGive);
            partialList.put(newItem, leftoverToGive);
        }

        List<ItemStack> keySet = new ArrayList<>(partialList.keySet());
        for(ItemStack itemKey : keySet) {
            int firstEmpty = inventory.firstEmpty();
            if(firstEmpty == -1) break;

            inventory.setItem(firstEmpty, itemKey);
            partialList.remove(itemKey);
        }

        return partialList;
    }

    private static ItemStack createItem(Material type, int amount) {
        if (type == null)
            return null;
        return new ItemStack(type, amount);
    }

    public static ItemStack createItem(String name, Material type, int amount) {
        ItemStack item = createItem(type, amount);
        if (name == null || item == null || item.getItemMeta() == null)
            return item;
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(String name, Material type, int amount, List<String> lore) {
        ItemStack item = createItem(name, type, amount);
        if (item == null || item.getItemMeta() == null)
            return item;
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(String name, Material type, int amount, String... lore) {
        return createItem(name, type, amount, Arrays.asList(lore));
    }

}
