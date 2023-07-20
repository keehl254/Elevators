package com.lkeehl.elevators;

import com.lkeehl.elevators.services.ElevatorActionService;
import com.lkeehl.elevators.services.ElevatorTypeService;
import com.lkeehl.elevators.services.ElevatorVersionService;
import com.lkeehl.elevators.services.ObstructionService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class Elevators extends JavaPlugin {

    @Override()
    public void onEnable() {
        ElevatorVersionService.init();
        ElevatorTypeService.init();
        ElevatorActionService.init();
        ObstructionService.init();

        // TODO: load config

        this.reloadElevators();
    }

    @Override()
    public void onDisable() {

    }

    private void reloadElevators() {

    }

    public static Elevators getInstance() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        if(plugin instanceof Elevators)
            return (Elevators) plugin;
        return null;
    }

    public static File getConfigDirectory() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        File configDirectory;
        if(plugin == null) {
            plugin = Bukkit.getPluginManager().getPlugins()[0];
            if(plugin == null)
                return null;
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

}
