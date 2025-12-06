package me.keehl.elevators.services;

import com.tcoded.folialib.FoliaLib;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.VersionHelper;
import me.keehl.elevators.services.hooks.*;
import org.bukkit.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ElevatorHooks {

    public static void buildHooksEarly(FoliaLib foliaLibs) {
        Elevators.pushAndHoldLog();
        ElevatorHookService.registerHook("Protect", ProtectHook.class, false);

        Elevators.popLog((logData) -> Elevators.log("Early Hooks built. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public static void buildHooks(FoliaLib foliaLibs) {
        Elevators.pushAndHoldLog();

        ElevatorHookService.registerHook("GriefPrevention", GriefPreventionHook.class);
        ElevatorHookService.registerHook("GriefDefender", GriefDefenderHook.class);
        ElevatorHookService.registerHook("RedProtect", RedProtectHook.class);
        ElevatorHookService.registerHook("PlotSquared", PlotSquaredHook.class);
        ElevatorHookService.registerHook("BentoBox", BentoBoxHook.class);
        ElevatorHookService.registerHook("SuperiorSkyblock2", SuperiorSkyblock2Hook.class, false);
        ElevatorHookService.registerHook("Lands", LandsHook.class, false);

        ElevatorHookService.registerHook("DecentHolograms", DecentHologramsHook.class);
        ElevatorHookService.registerHook("FancyHolograms", FancyHologramsHook.class);

        ElevatorHookService.registerHook("PlaceholderAPI", PlaceholderAPIHook.class);

        ElevatorHookService.registerHook("ItemsAdder", ItemsAdderHook.class);
        ElevatorHookService.registerHook("Oraxen", OraxenHook.class);
        ElevatorHookService.registerHook("Nexo", NexoHook.class);

        if(VersionHelper.doesVersionSupportDialogs()) {
            if (foliaLibs.isPaper()) {
                ElevatorHookService.setDialogHook(new PaperDialogHook());
            } else if (foliaLibs.isSpigot()) {
                ElevatorHookService.setDialogHook(new SpigotDialogHook());
            }
        }

        Elevators.popLog((logData) -> Elevators.log("Hooks built. "+ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

        // I don't care to risk the class failing to load due to a classnotfound error from the event.
        if(VersionHelper.doesVersionSupportAutoCrafters()) {
            try {
                Class<?> clazz = Class.forName("me.keehl.elevators.services.listeners.AutoCrafterListener");
                Method method = clazz.getMethod("setupListener");
                method.invoke(null);
            } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
