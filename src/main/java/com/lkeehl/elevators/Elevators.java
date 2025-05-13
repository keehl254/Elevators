package com.lkeehl.elevators;

import com.lkeehl.elevators.services.*;
import com.tcoded.folialib.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class Elevators extends JavaPlugin {

    private static Elevators instance;
    private static FoliaLib foliaLib;

    private boolean initialized = false;

    @Override()
    public void onEnable() {
        instance = this;
        foliaLib = new FoliaLib(this);

        ElevatorDataContainerService.init(this);
        ElevatorSettingService.init();
        ElevatorVersionService.init();
        ElevatorEffectService.init();
        ElevatorActionService.init();
        ElevatorTypeService.init();
        ElevatorRecipeService.init();
        ElevatorObstructionService.init();
        ElevatorHookService.init();
        ElevatorListenerService.init();
        ElevatorHologramService.init();
        ElevatorCommandService.init(this);

        this.reloadElevators();
        this.initialized = true;
    }

    @Override()
    public void onDisable() {
        ElevatorHookService.unInitialize();
        ElevatorListenerService.unInitialize();
        ElevatorHologramService.onDisable();

        this.saveConfig();
        this.initialized = false;
    }

    public void saveConfig() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        ElevatorConfigService.saveConfig(configFile);
    }

    public void reloadElevators() {

        File configFile = new File(this.getDataFolder(), "config.yml");
        this.saveDefaultConfig();

        ElevatorConfigService.loadConfig(configFile);
        this.saveConfig();
    }

    public static Elevators getInstance() { // I consider it bad practice to rely on a static instance, so I am prioritizing using getPlugin.
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        if(plugin instanceof Elevators)
            return (Elevators) plugin;
        return instance;
    }

    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public static File getConfigDirectory() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        File configDirectory;
        if(plugin == null) {
            plugin = Bukkit.getPluginManager().getPlugins()[0];
            configDirectory = new File(plugin.getDataFolder().getParent(),"Elevators");
        }else
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
        if(plugin != null)
            return plugin.getLogger();

        return Bukkit.getLogger();

    }

    public boolean isInitialized() {
        return this.initialized;
    }
}
