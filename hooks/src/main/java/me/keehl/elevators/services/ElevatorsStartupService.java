package me.keehl.elevators.services;

import com.tcoded.folialib.FoliaLib;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.*;
import me.keehl.elevators.commands.ElevatorCommand;
import me.keehl.elevators.events.ElevatorMenuOpenEvent;
import me.keehl.elevators.helpers.VersionHelper;
import me.keehl.elevators.hooks.*;
import me.keehl.elevators.listeners.ElevatorMenuOpenListener;
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
        Elevators.pushAndHoldLog();
        ElevatorHookService.registerHook("Protect", ProtectHook.class, false);

        Elevators.popLog((logData) -> Elevators.log("Early Hooks built. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    private static void buildHooks(FoliaLib foliaLibs) {
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

        Elevators.popLog((logData) -> Elevators.log("Hooks built. "+ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

        ElevatorListenerService.registerEventExecutor(ElevatorMenuOpenEvent.class, EventPriority.MONITOR, ElevatorMenuOpenListener::onInteractMenuOpen);

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
        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "sound", SoundAction::new, ChatColor.GREEN.toString(), "Sound", Material.MUSIC_DISC_MALL);
        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "command-console", CommandConsoleAction::new, ChatColor.DARK_RED.toString(), "Console Command", Material.COMMAND_BLOCK);
        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "command-player", CommandPlayerAction::new, ChatColor.LIGHT_PURPLE.toString(), "Player Command", Material.REPEATING_COMMAND_BLOCK);
        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "message-player", MessagePlayerAction::new, ChatColor.YELLOW.toString(), "Message User", Material.WRITTEN_BOOK);
        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "message-all", MessageAllAction::new, ChatColor.RED.toString(), "Broadcast Message", Material.ENCHANTED_BOOK);

        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "effect", EffectAction::new, ChatColor.BLUE.toString(), "Effect", Material.FIREWORK_ROCKET);

        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "title", TitleAction::new, ChatColor.LIGHT_PURPLE.toString(), "Title", Material.NAME_TAG);
        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "action-bar", ActionBarAction::new, ChatColor.YELLOW.toString(), "Action Bar", Material.BELL);
        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "boss-bar", BossBarAction::new, ChatColor.RED.toString(), "Boss Bar", Material.DRAGON_HEAD);

        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "charge-exp", ChargeExpAction::new, ChatColor.GOLD.toString(), "Charge EXP", Material.EXPERIENCE_BOTTLE);
        ElevatorActionService.registerElevatorAction(Elevators.getInstance(), "trigger-observer", TriggerObserverAction::new, ChatColor.RED.toString(), "Trigger Observer", Material.OBSERVER);
    }

    public static void buildSettings() {
        ElevatorSettingService.addSetting(new UsePermissionSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new DyePermissionSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new CanExplodeSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new CheckColorSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new CheckPermsSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new ClassCheckSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new DisplayNameSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new LoreLinesSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new MaxDistanceSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new MaxSolidBlocksSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new MaxStackSizeSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new StopObstructionSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new SupportDyingSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new AllowIndividualEditSetting(Elevators.getInstance()));
        ElevatorSettingService.addSetting(new HologramLinesSetting(Elevators.getInstance()));
    }

    public static void buildElevatorsEarly(JavaPlugin plugin, FoliaLib foliaLib) {

        if(ElevatorActionService.registerDefaultActionsRunnable == null) {
            ElevatorActionService.registerDefaultActionsRunnable = ElevatorsStartupService::buildActions;
        }
        if(ElevatorSettingService.registerDefaultSettingsRunnable == null) {
            ElevatorSettingService.registerDefaultSettingsRunnable = ElevatorsStartupService::buildSettings;
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
