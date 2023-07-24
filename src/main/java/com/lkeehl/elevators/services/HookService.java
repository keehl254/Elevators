package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.hooks.*;
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
        HookService.registerHookIfPluginActive("GriefDefender", GriefDefenderHook.class);
        HookService.registerHookIfPluginActive("RedProtect", RedProtectHook.class);
        HookService.registerHookIfPluginActive("PlotSquared", PlotSquaredHook.class);
        HookService.registerHookIfPluginActive("BentoBox", BentoBoxHook.class);

    }

    public static boolean registerHookIfPluginActive(String pluginName, Class<? extends ElevatorHook> elevatorHookClass) {
       if(hookMap.containsKey(pluginName.toUpperCase()))
           return true;

       if(!Bukkit.getPluginManager().isPluginEnabled(pluginName))
           return false;

        try {
            Constructor<?> hookConstructor = elevatorHookClass.getConstructor();
            hookMap.put(pluginName.toUpperCase(), (ElevatorHook) hookConstructor.newInstance());
            return true;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Elevators.getElevatorsLogger().warning("Failed to register hook for \"" + pluginName + "\" due to an inaccessible constructor.");
            return false;
        }
    }

    public static boolean canUseElevator(Player player, ShulkerBox shulkerBox, ElevatorType elevatorType) {
        return hookMap.values().stream().anyMatch(hook -> !hook.canPlayerUseElevator(player, shulkerBox, elevatorType));
    }

    public static GriefPreventionHook getGriefPreventionHook() {
        return getHook("GriefPrevention", GriefPreventionHook.class);
    }

    public static GriefPreventionHook getGriefDefenderHook() {
        return getHook("GriefPrevention", GriefPreventionHook.class);
    }

    public static RedProtectHook getRedProtectHook() {
        return getHook("RedProtect", RedProtectHook.class);
    }

    public static PlotSquaredHook getPlotSquaredHook() {
        return getHook("PlotSquared", PlotSquaredHook.class);
    }

    public static BentoBoxHook getBentoBoxHook() {
        return getHook("BentoBox", BentoBoxHook.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ElevatorHook> T getHook(String hookKey, Class<T> hookClazz) {
        hookKey = hookKey.toUpperCase();
        if(!hookMap.containsKey(hookKey.toUpperCase()) && !HookService.registerHookIfPluginActive(hookKey, hookClazz))
            return null;

        return (T) hookMap.get(hookKey);
    }

}
