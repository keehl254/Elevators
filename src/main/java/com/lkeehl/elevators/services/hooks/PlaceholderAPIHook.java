package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlaceholderAPIHook implements ElevatorHook {

    public String formatPlaceholders(Player player, String message){
        return PlaceholderAPI.setPlaceholders(player, message);
    }

    @Override
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        return true;
    }

    @Override
    public ItemStack createIconForElevator(Player player, Elevator elevator) {
        return null;
    }

}
