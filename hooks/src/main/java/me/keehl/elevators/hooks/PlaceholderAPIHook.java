package me.keehl.elevators.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.keehl.elevators.models.hooks.PlaceholderHook;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderHook {

    public String formatPlaceholders(Player player, String message){
        return PlaceholderAPI.setPlaceholders(player, message);
    }

    @Override
    public void onInit() {
    }

}
