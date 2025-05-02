package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ResourceHelper;
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
import java.util.logging.Level;
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

    public static void unInitialize() {
        hookMap.clear();

        HookService.initialized = false;
    }

    private static void buildHooks() {

        HookService.registerHook("GriefPrevention", GriefPreventionHook.class);
        HookService.registerHook("GriefDefender", GriefDefenderHook.class);
        HookService.registerHook("RedProtect", RedProtectHook.class);
        HookService.registerHook("PlotSquared", PlotSquaredHook.class);
        HookService.registerHook("BentoBox", BentoBoxHook.class);
        HookService.registerHook("PlaceholderAPI", PlaceholderAPIHook.class);
        HookService.registerHook("DecentHolograms", DecentHologramsHook.class);
        HookService.registerHook("FancyHolograms", FancyHologramsHook.class);
        HookService.registerHook("SuperiorSkyblock2", SuperiorSkyblock2Hook.class, false);

    }

    public static boolean registerHook(String pluginName, Class<? extends ElevatorHook> elevatorHookClass, boolean requireActive) {
       if(hookMap.containsKey(pluginName.toUpperCase()))
           return true;

        if(Bukkit.getPluginManager().getPlugin(pluginName) == null)
            return false;
       if(requireActive && !Bukkit.getPluginManager().isPluginEnabled(pluginName))
           return false;

        try {
            Constructor<?> hookConstructor = elevatorHookClass.getConstructor();
            hookMap.put(pluginName.toUpperCase(), (ElevatorHook) hookConstructor.newInstance());
            return true;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Elevators.getElevatorsLogger().log(Level.WARNING, "Failed to register hook for \"" + pluginName + "\" due to an inaccessible constructor. The plugin will still function; however, this hook will not work. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues", e);
            return false;
        }
    }

    public static boolean registerHook(String pluginName, Class<? extends ElevatorHook> elevatorHookClass) {
        return registerHook(pluginName, elevatorHookClass, true);
    }

    public static boolean canUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        try {
            return hookMap.values().stream().allMatch(hook -> hook.canPlayerUseElevator(player, elevator, sendMessage));
        } catch (Exception e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to check hooks for use permission. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return false;
    }

    public static boolean canEditElevator(Player player, Elevator elevator, boolean sendMessage) {
        try {
            return HookService.getProtectionHooks().stream().allMatch(hook -> hook.canEditSettings(player, elevator, sendMessage));
        } catch (Exception e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to check hooks for edit permission. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return false;
    }

    public static boolean canRenameElevator(Player player, Elevator elevator, boolean sendMessage) {
        try {
            return HookService.getProtectionHooks().stream().allMatch(hook -> hook.canEditName(player, elevator, sendMessage));
        } catch (Exception e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to check hooks for rename permission. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return false;
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
            HookService.buildHooks(); // No need to check individual hooks here. BuildHooks will not re-register.
            if(!hookMap.containsKey(hookKey.toUpperCase()))
                return null;
        }

        return (T) hookMap.get(hookKey);
    }

    public static List<ProtectionHook> getProtectionHooks() {
        return hookMap.values().stream().filter(i -> i instanceof ProtectionHook).map(i -> (ProtectionHook) i).collect(Collectors.toList());
    }

}
