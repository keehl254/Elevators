package me.keehl.elevators.services;

import com.tcoded.folialib.FoliaLib;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.*;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.commands.ElevatorCommand;
import me.keehl.elevators.events.ElevatorMenuOpenEvent;
import me.keehl.elevators.helpers.VersionHelper;
import me.keehl.elevators.hooks.*;
import me.keehl.elevators.settings.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class ElevatorsStartupService {

    private static void buildHooksEarly(FoliaLib foliaLibs) {
        ElevatorsAPI.pushAndHoldLog();
        Elevators.getHooksService().registerHook("Protect", ProtectHook.class, false);
        Elevators.getHooksService().registerHook("WorldGuard", WorldGuardHook.class, false);

        ElevatorsAPI.popLog((logData) -> ElevatorsAPI.log("Early Hooks built. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    private static void buildHooks(FoliaLib foliaLibs) {
        ElevatorsAPI.pushAndHoldLog();

        Elevators.getHooksService().registerHook("GriefPrevention", GriefPreventionHook.class);
        Elevators.getHooksService().registerHook("GriefDefender", GriefDefenderHook.class);
        Elevators.getHooksService().registerHook("RedProtect", RedProtectHook.class);
        Elevators.getHooksService().registerHook("PlotSquared", PlotSquaredHook.class);
        Elevators.getHooksService().registerHook("BentoBox", BentoBoxHook.class);
        Elevators.getHooksService().registerHook("SuperiorSkyblock2", SuperiorSkyblock2Hook.class, false);
        Elevators.getHooksService().registerHook("Lands", LandsHook.class, false);

        Elevators.getHooksService().registerHook("DecentHolograms", DecentHologramsHook.class);
        Elevators.getHooksService().registerHook("FancyHolograms", FancyHologramsHook.class);

        Elevators.getHooksService().registerHook("PlaceholderAPI", PlaceholderAPIHook.class);

        Elevators.getHooksService().registerHook("ItemsAdder", ItemsAdderHook.class);
        Elevators.getHooksService().registerHook("Oraxen", OraxenHook.class);
        Elevators.getHooksService().registerHook("Nexo", NexoHook.class);

        ElevatorsAPI.popLog((logData) -> ElevatorsAPI.log("Hooks built. "+ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

        // I don't care to risk the class failing to load due to a classnotfound error from the event.
        if(VersionHelper.doesVersionSupportAutoCrafters()) {
            try {
                Class<?> clazz = Class.forName("me.keehl.elevators.listeners.AutoCrafterListener");
                Method method = clazz.getMethod("setupListener");
                method.invoke(null);
            } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void buildActions() {
        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "sound", SoundAction::new, ChatColor.GREEN.toString(), "Sound", Material.MUSIC_DISC_MALL);
        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "command-console", CommandConsoleAction::new, ChatColor.DARK_RED.toString(), "Console Command", Material.COMMAND_BLOCK);
        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "command-player", CommandPlayerAction::new, ChatColor.LIGHT_PURPLE.toString(), "Player Command", Material.REPEATING_COMMAND_BLOCK);
        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "message-player", MessagePlayerAction::new, ChatColor.YELLOW.toString(), "Message User", Material.WRITTEN_BOOK);
        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "message-all", MessageAllAction::new, ChatColor.RED.toString(), "Broadcast Message", Material.ENCHANTED_BOOK);

        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "effect", EffectAction::new, ChatColor.BLUE.toString(), "Effect", Material.FIREWORK_ROCKET);

        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "title", TitleAction::new, ChatColor.LIGHT_PURPLE.toString(), "Title", Material.NAME_TAG);
        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "action-bar", ActionBarAction::new, ChatColor.YELLOW.toString(), "Action Bar", Material.BELL);
        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "boss-bar", BossBarAction::new, ChatColor.RED.toString(), "Boss Bar", Material.DRAGON_HEAD);

        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "charge-exp", ChargeExpAction::new, ChatColor.GOLD.toString(), "Charge EXP", Material.EXPERIENCE_BOTTLE);
        Elevators.getActionService().registerElevatorAction(Elevators.getInstance(), "trigger-observer", TriggerObserverAction::new, ChatColor.RED.toString(), "Trigger Observer", Material.OBSERVER);
    }

    public static void buildSettings() {
        Elevators.getSettingService().addSetting(new UsePermissionSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new DyePermissionSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new CanExplodeSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new CheckColorSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new CheckPermsSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new ClassCheckSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new DisplayNameSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new LoreLinesSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new MaxDistanceSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new MaxSolidBlocksSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new MaxStackSizeSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new StopObstructionSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new SupportDyingSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new AllowIndividualEditSetting(Elevators.getInstance()));
        Elevators.getSettingService().addSetting(new HologramLinesSetting(Elevators.getInstance()));
    }

    public static void buildElevatorsEarly(JavaPlugin plugin, FoliaLib foliaLib) {

        if(Elevators.getActionService().registerDefaultActionsRunnable == null) {
            Elevators.getActionService().registerDefaultActionsRunnable = ElevatorsStartupService::buildActions;
        }
        if(Elevators.getSettingService().registerDefaultSettingsRunnable == null) {
            Elevators.getSettingService().registerDefaultSettingsRunnable = ElevatorsStartupService::buildSettings;
        }

        buildHooksEarly(foliaLib);
    }

    public static void buildElevators(JavaPlugin plugin, FoliaLib foliaLib) {
        buildHooks(foliaLib);

        ElevatorCommand commands = new ElevatorCommand();
        Objects.requireNonNull(plugin.getCommand("elevators")).setExecutor(commands);
        Objects.requireNonNull(plugin.getCommand("elevators")).setTabCompleter(commands);
    }

}
