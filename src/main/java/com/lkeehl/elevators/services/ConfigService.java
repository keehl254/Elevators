package com.lkeehl.elevators.services;

import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.configs.ConfigEffect;
import com.lkeehl.elevators.services.configs.ConfigElevatorType;
import com.lkeehl.elevators.services.configs.ConfigLocale;
import com.lkeehl.elevators.services.configs.ConfigRoot;
import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ConfigRootNode;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigService {

    private static ConfigRootNode<ConfigRoot> rootNode;

    private static ConfigLocale defaultLocaleConfig;

    private static final List<Consumer<ConfigRoot>> configLoadCallbacks = new ArrayList<>();

    public static void loadConfig(File configFile) {
        boolean configCurrentlyExists = configFile.exists();

        try {
            ConfigService.rootNode = ConfigConverter.createNodeForConfig(new ConfigRoot(), configFile);
            configLoadCallbacks.forEach(i -> i.accept(ConfigService.rootNode.getConfig()));

            //ConfigService.rootNode.save();
            ConfigConverter.saveConfigToFile(ConfigService.rootNode, configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig(File configFile) {
        ConfigConverter.saveConfigToFile(ConfigService.rootNode, configFile);
    }

    public static void addConfigCallback(Consumer<ConfigRoot> callback) {
        ConfigService.configLoadCallbacks.add(callback);
        if(ConfigService.rootNode != null)
            callback.accept(ConfigService.rootNode.getConfig());
    }

    public static ConfigRoot getRootConfig() {
        return ConfigService.rootNode.getConfig();
    }

    public static ConfigLocale getDefaultLocaleConfig() {
        if(ConfigService.defaultLocaleConfig == null)
            ConfigService.defaultLocaleConfig = new ConfigLocale();

        return ConfigService.defaultLocaleConfig;
    }

    public static Map<String, ConfigEffect> getEffectConfigs() {
        return ConfigService.getRootConfig().effects;
    }

    public static Map<String, ElevatorType> getElevatorTypeConfigs() {
        return ConfigService.getRootConfig().elevators;
    }

    public static boolean isWorldDisabled(World world) {
        return ConfigService.getRootConfig().disabledWorlds.stream().anyMatch(i -> i.equalsIgnoreCase(world.getName()));
    }

}
