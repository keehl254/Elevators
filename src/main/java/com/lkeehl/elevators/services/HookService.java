package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ElevatorHook;
import com.lkeehl.elevators.models.hooks.HologramHook;
import com.lkeehl.elevators.services.hooks.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class HookService {

    private static boolean initialized = false;

    private static final Map<String, ElevatorHook> hookMap = new HashMap<>();

    private static boolean isPaper = false;

    public static void init() {
        if(HookService.initialized)
            return;

        HookService.buildHooks();

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            isPaper = true;
        } catch (ClassNotFoundException ignored) {
        }

        HookService.initialized = true;
    }

    private static void buildHooks() {

        HookService.registerHookIfPluginActive("GriefPrevention", GriefPreventionHook.class);
        HookService.registerHookIfPluginActive("GriefDefender", GriefDefenderHook.class);
        HookService.registerHookIfPluginActive("RedProtect", RedProtectHook.class);
        HookService.registerHookIfPluginActive("PlotSquared", PlotSquaredHook.class);
        HookService.registerHookIfPluginActive("BentoBox", BentoBoxHook.class);
        HookService.registerHookIfPluginActive("PlaceholderAPI", PlaceholderAPIHook.class);
        HookService.registerHookIfPluginActive("DecentHolograms", DecentHologramsHook.class);

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

    public static boolean canUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        return hookMap.values().stream().allMatch(hook -> hook.canPlayerUseElevator(player, elevator, sendMessage));
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

    public static PlaceholderAPIHook getPlaceholderAPIHook() {
        return getHook("PlaceholderAPI", PlaceholderAPIHook.class);
    }

    public static HologramHook<?> getHologramHook() {
        HologramHook<?> hook = getHook("DecentHolograms", DecentHologramsHook.class);
        /*if(hook == null) {
             TODO: Check FancyHolograms when their API is accessible.
        }*/

        return hook;
    }

    public static boolean isServerRunningPaper() {
        return isPaper;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ElevatorHook> T getHook(String hookKey, Class<T> hookClazz) {
        hookKey = hookKey.toUpperCase();
        if(!hookMap.containsKey(hookKey.toUpperCase()) && !HookService.registerHookIfPluginActive(hookKey, hookClazz))
            return null;

        return (T) hookMap.get(hookKey);
    }

}
