package me.keehl.elevators.api.services;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.hooks.ElevatorHook;
import me.keehl.elevators.api.models.hooks.ItemsHook;
import me.keehl.elevators.api.models.hooks.PlaceholderHook;
import me.keehl.elevators.api.models.hooks.IProtectionHook;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IElevatorHookService extends IElevatorService {

    void registerHook(String pluginName, Class<? extends ElevatorHook> elevatorHookClass, boolean requireActive);

    void registerHook(String pluginName, Class<? extends ElevatorHook> elevatorHookClass);

    boolean canUseElevator(Player player, IElevator elevator, boolean sendMessage);

    boolean canEditElevator(Player player, IElevator elevator, boolean sendMessage);

    boolean canRenameElevator(Player player, IElevator elevator, boolean sendMessage);

    ItemStack createItemStackFromKey(NamespacedKey key);

    NamespacedKey getKeyFromItemStack(ItemStack item);

    PlaceholderHook getPlaceholderHook();

    List<IProtectionHook> getProtectionHooks();

    List<ItemsHook> getItemsHooks();
}
