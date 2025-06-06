package me.keehl.elevators;

import me.keehl.elevators.services.*;
import com.tcoded.folialib.FoliaLib;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class Elevators {

    protected static JavaPlugin instance;
    protected static FoliaLib foliaLib;
    protected static Metrics metrics;

    protected static boolean initialized = false;

    protected static void enable(JavaPlugin plugin) {
        instance = plugin;
        foliaLib = new FoliaLib(plugin);
        metrics = new Metrics(plugin, 8026);

        ElevatorDataContainerService.init();
        ElevatorSettingService.init();
        ElevatorVersionService.init();
        ElevatorEffectService.init();
        ElevatorActionService.init();
        ElevatorTypeService.init();
        ElevatorRecipeService.init();
        ElevatorObstructionService.init();
        ElevatorListenerService.init();
        ElevatorHookService.init();
        ElevatorHologramService.init();
        ElevatorCommandService.init();
        ElevatorUpdateService.init(plugin.getDescription().getVersion());

        reloadElevators();
        initialized = true;
    }

    public static void disable() {
        ElevatorHookService.unInitialize();
        ElevatorListenerService.unInitialize();
        ElevatorHologramService.onDisable();
        ElevatorUpdateService.unInitialize();

        saveConfig();
        initialized = false;
    }

    public static void saveConfig() {
        File configFile = new File(instance.getDataFolder(), "config.yml");
        ElevatorConfigService.saveConfig(configFile);
    }

    public static void reloadElevators() {

        File configFile = new File(instance.getDataFolder(), "config.yml");
        instance.saveDefaultConfig();

        ElevatorConfigService.loadConfig(configFile);
        saveConfig();
    }

    public static JavaPlugin getInstance() { // I consider it bad practice to rely on a static instance, so I am prioritizing using getPlugin.
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        if (plugin != null)
            return (JavaPlugin) plugin;
        return instance;
    }

    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public static File getConfigDirectory() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        File configDirectory;
        if (plugin == null) {
            plugin = Bukkit.getPluginManager().getPlugins()[0];
            configDirectory = new File(plugin.getDataFolder().getParent(), "Elevators");
        } else
            configDirectory = plugin.getDataFolder();
        try {
            //noinspection ResultOfMethodCallIgnored
            configDirectory.mkdirs();
        } catch (Exception ignored) {
        }

        return configDirectory;
    }

    public static Logger getElevatorsLogger() {

        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        if (plugin != null)
            return plugin.getLogger();

        return Bukkit.getLogger();

    }

    public static boolean isInitialized() {
        return initialized;
    }
}
