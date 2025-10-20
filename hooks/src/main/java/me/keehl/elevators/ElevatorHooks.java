package me.keehl.elevators;

import me.keehl.elevators.services.ElevatorHookService;
import me.keehl.elevators.services.hooks.*;
import org.bukkit.ChatColor;

public class ElevatorHooks {

    public static void buildHooksEarly() {
        Elevators.pushAndHoldLog();
        ElevatorHookService.registerHook("Protect", ProtectHook.class, false);
    }

    public static void buildHooks() {
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

        Elevators.popLog((logData) -> Elevators.log("Hooks built. "+ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

}
