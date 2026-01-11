package me.keehl.elevators.hooks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
class ItemStackHelper {

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
