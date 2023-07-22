package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.hooks.GriefPreventionHook;
import com.lkeehl.elevators.services.hooks.PlotSquaredHook;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class HookService {

    private static boolean initialized = false;

    private static final Map<String, ElevatorHook> hookMap = new HashMap<>();

    public static void init() {
        if(HookService.initialized)
            return;

        HookService.buildHooks();

        HookService.initialized = true;
    }

    private static void buildHooks() {

        HookService.registerHookIfPluginActive("GriefPrevention", GriefPreventionHook.class);
        HookService.registerHookIfPluginActive("PlotSquared", PlotSquaredHook.class);


    }

    public static void registerHookIfPluginActive(String pluginName, Class<? extends ElevatorHook> elevatorHook) {
       if(hookMap.containsKey(pluginName.toUpperCase()))
           return;

       if(!Bukkit.getPluginManager().isPluginEnabled(pluginName))
           return;

        try {
            Constructor<?> hookConstructor = elevatorHook.getConstructor();
            hookMap.put(pluginName.toUpperCase(), (ElevatorHook) hookConstructor.newInstance());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Elevators.getElevatorsLogger().warning("Failed to register hook for \"" + pluginName + "\" due to an inaccessible constructor.");
        }
    }

    public static boolean canUseElevator(Player player, ShulkerBox shulkerBox, ElevatorType elevatorType) {
        return hookMap.values().stream().anyMatch(hook -> !hook.canPlayerUseElevator(player, shulkerBox, elevatorType));
    }

}
