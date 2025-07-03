package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.hooks.*;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ElevatorHookService {

    private static boolean initialized = false;

    private static final Map<String, ElevatorHook> hookMap = new HashMap<>();

    private static PlaceholderHook placeholderHook = null;
    private static HologramHook hologramHook = null;

    public static void init() {
        if(ElevatorHookService.initialized)
            return;

        ElevatorHookService.initialized = true;
    }

    public static void unInitialize() {
        hookMap.clear();

        ElevatorHookService.initialized = false;
    }

    public static void registerHook(String pluginName, Class<? extends ElevatorHook> elevatorHookClass, boolean requireActive) {
       if(hookMap.containsKey(pluginName.toUpperCase()))
           return;

        if(Bukkit.getPluginManager().getPlugin(pluginName) == null)
            return;
       if(requireActive && !Bukkit.getPluginManager().isPluginEnabled(pluginName))
           return;

        try {
            Constructor<?> hookConstructor = elevatorHookClass.getConstructor();
            hookMap.put(pluginName.toUpperCase(), (ElevatorHook) hookConstructor.newInstance());
            Elevators.getElevatorsLogger().info("Hooked into " + pluginName);

            placeholderHook = hookMap.values().stream().filter(i -> i instanceof PlaceholderHook).map(i -> (PlaceholderHook) i).findFirst().orElse(null);
            hologramHook = hookMap.values().stream().filter(i -> i instanceof HologramHook).map(i -> (HologramHook) i).findFirst().orElse(null);

        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Elevators.getElevatorsLogger().log(Level.WARNING, "Failed to register hook for \"" + pluginName + "\" due to an inaccessible constructor. The plugin will still function; however, this hook will not work. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues", e);
        }
    }

    public static void registerHook(String pluginName, Class<? extends ElevatorHook> elevatorHookClass) {
        registerHook(pluginName, elevatorHookClass, true);
    }

    public static boolean canUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        try {
            return ElevatorHookService.getProtectionHooks().stream().allMatch(hook -> !hook.isCheckEnabled(elevator) || hook.canPlayerUseElevator(player, elevator, sendMessage));
        } catch (Exception e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to check hooks for use permission. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return false;
    }

    public static boolean canEditElevator(Player player, Elevator elevator, boolean sendMessage) {
        try {
            return ElevatorHookService.getProtectionHooks().stream().allMatch(hook -> hook.canEditSettings(player, elevator, sendMessage));
        } catch (Exception e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to check hooks for edit permission. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return false;
    }

    public static boolean canRenameElevator(Player player, Elevator elevator, boolean sendMessage) {
        try {
            return ElevatorHookService.getProtectionHooks().stream().allMatch(hook -> hook.canEditName(player, elevator, sendMessage));
        } catch (Exception e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to check hooks for rename permission. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return false;
    }

    public static ItemStack createItemStackFromKey(NamespacedKey key) {

        if(key.getNamespace().equalsIgnoreCase(NamespacedKey.MINECRAFT)) {
            Material type = Material.matchMaterial(key.getKey());
            if(type == null)
                return null;
            return new ItemStack(type,1);
        }

        if(key.getNamespace().equalsIgnoreCase(Elevators.getInstance().getName().toLowerCase(Locale.ROOT))) {
            ElevatorType type = ElevatorTypeService.getElevatorType(key.getKey());
            if(type != null)
                return ItemStackHelper.createItemStackFromElevatorType(type, DyeColor.RED);
            return null;
        }

        try {
            for(ItemsHook hook : ElevatorHookService.getItemsHooks()) {
                ItemStack item = hook.createItemStackFromKey(key);
                if(item != null)
                    return item;
            }
        } catch (Exception e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to get item from namespaced key. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return null;
    }

    public static NamespacedKey getKeyFromItemStack(ItemStack item) {
        try {
            for(ItemsHook hook : ElevatorHookService.getItemsHooks()) {
                NamespacedKey key = hook.getKeyFromItemStack(item);
                if(key != null)
                    return key;
            }
        } catch (Exception e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to get namespaced key from itemstack. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return item.getType().getKey();
    }

    public static PlaceholderHook getPlaceholderHook() {
        return placeholderHook;
    }

    // Protected because we want all hologram alterations to be done through HologramService
    protected static HologramHook getHologramHook() {
        return hologramHook;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ElevatorHook> T getHook(String hookKey) {
        hookKey = hookKey.toUpperCase();
        if(!hookMap.containsKey(hookKey.toUpperCase()))
            return null;

        return (T) hookMap.get(hookKey);
    }

    public static List<ProtectionHook> getProtectionHooks() {
        return hookMap.values().stream().filter(i -> i instanceof ProtectionHook).map(i -> (ProtectionHook) i).collect(Collectors.toList());
    }

    public static List<ItemsHook> getItemsHooks() {
        return hookMap.values().stream().filter(i -> i instanceof ItemsHook).map(i -> (ItemsHook) i).collect(Collectors.toList());
    }

}
