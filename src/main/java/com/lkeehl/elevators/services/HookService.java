package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ElevatorHook;
import com.lkeehl.elevators.models.hooks.HologramHook;
import com.lkeehl.elevators.models.hooks.ProtectionHook;
import com.lkeehl.elevators.services.hooks.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        HookService.registerHookIfPluginActive("GriefPrevention", false, GriefPreventionHook.class);
        HookService.registerHookIfPluginActive("GriefDefender", false, GriefDefenderHook.class);
        HookService.registerHookIfPluginActive("RedProtect", false, RedProtectHook.class);
        HookService.registerHookIfPluginActive("PlotSquared", false, PlotSquaredHook.class);
        HookService.registerHookIfPluginActive("BentoBox", false, BentoBoxHook.class);
        HookService.registerHookIfPluginActive("PlaceholderAPI", false, PlaceholderAPIHook.class);
        HookService.registerHookIfPluginActive("DecentHolograms", false, DecentHologramsHook.class);
        HookService.registerHookIfPluginActive("FancyHolograms", false, FancyHologramsHook.class);
        HookService.registerHookIfPluginActive("SuperiorSkyblock2", true, SuperiorSkyblock2Hook.class);
    }

    public static boolean registerHookIfPluginActive(String pluginName, boolean needOnlyLoad, Class<? extends ElevatorHook> elevatorHookClass) {
       if(hookMap.containsKey(pluginName.toUpperCase()))
           return true;

       if(needOnlyLoad) {
           if(Bukkit.getPluginManager().getPlugin(pluginName) == null) return false;
       } else {
           if(!Bukkit.getPluginManager().isPluginEnabled(pluginName)) return false;
       }

        try {
            Constructor<?> hookConstructor = elevatorHookClass.getConstructor();
            hookMap.put(pluginName.toUpperCase(), (ElevatorHook) hookConstructor.newInstance());
            return true;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Elevators.getElevatorsLogger().warning("Failed to register hook for \"" + pluginName + "\" due to an inaccessible constructor. The plugin will still function; however, this hook will not work. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues");
            e.printStackTrace();
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

    public static SuperiorSkyblock2Hook getSuperiorSkyblock2Hook() {
        return getHook("SuperiorSkyblock2", SuperiorSkyblock2Hook.class);
    }

    public static PlaceholderAPIHook getPlaceholderAPIHook() {
        return getHook("PlaceholderAPI", PlaceholderAPIHook.class);
    }

    public static HologramHook<?> getHologramHook() {
        HologramHook<?> hook = getHook("DecentHolograms", DecentHologramsHook.class);
        if(hook == null)
            hook = getHook("FancyHolograms", FancyHologramsHook.class);

        return hook;
    }

    public static boolean isServerRunningPaper() {
        return isPaper;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ElevatorHook> T getHook(String hookKey, Class<T> hookClazz) {
        hookKey = hookKey.toUpperCase();
        if(!hookMap.containsKey(hookKey.toUpperCase())) {
            if (hookKey.equals("SUPERIORSKYBLOCK2")) {
                if(!HookService.registerHookIfPluginActive(hookKey, true, hookClazz)) return null;
            } else {
                if(!HookService.registerHookIfPluginActive(hookKey, false, hookClazz)) return null;
            }
        }

        return (T) hookMap.get(hookKey);
    }

    public static List<ProtectionHook> getProtectionHooks() {
        return hookMap.values().stream().filter(i -> i instanceof ProtectionHook).map(i -> (ProtectionHook) i).collect(Collectors.toList());
    }

}
