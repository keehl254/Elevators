package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.configs.ConfigVersionBuilder;
import me.keehl.elevators.services.configs.versions.configv5.ConfigEffect;
import me.keehl.elevators.services.configs.versions.configv5.ConfigLocale;
import me.keehl.elevators.services.configs.versions.configv5.ConfigRoot;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ConfigRootNode;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ElevatorConfigService {

    private static boolean invalidConfig = false;

    private static ConfigRootNode<ConfigRoot> rootNode;

    private static ConfigLocale defaultLocaleConfig;

    private static final List<Consumer<ConfigRoot>> configLoadCallbacks = new ArrayList<>();

    public static void loadConfig(File configFile) {
        ElevatorConfigService.rootNode = ConfigVersionBuilder.getConfig(configFile);
        if(ElevatorConfigService.rootNode == null) {
            Bukkit.getPluginManager().disablePlugin(Elevators.getInstance());
            return;
        }

        configLoadCallbacks.forEach(i -> i.accept(ElevatorConfigService.rootNode.getConfig()));
        ConfigConverter.saveConfigToFile(ElevatorConfigService.rootNode, configFile);
    }

    public static void invalidateConfig() {
        invalidConfig = true;
    }

    public static void saveConfig(File configFile) {
        if(invalidConfig)
            return;

        ConfigConverter.saveConfigToFile(ElevatorConfigService.rootNode, configFile);
    }

    public static void addConfigCallback(Consumer<ConfigRoot> callback) {
        ElevatorConfigService.configLoadCallbacks.add(callback);
        if(ElevatorConfigService.rootNode != null)
            callback.accept(ElevatorConfigService.rootNode.getConfig());
    }

    public static ConfigRoot getRootConfig() {
        return ElevatorConfigService.rootNode.getConfig();
    }

    public static boolean isConfigLoaded() {
        return ElevatorConfigService.rootNode != null;
    }

    public static ConfigLocale getDefaultLocaleConfig() {
        if(ElevatorConfigService.defaultLocaleConfig == null)
            ElevatorConfigService.defaultLocaleConfig = new ConfigLocale();

        return ElevatorConfigService.defaultLocaleConfig;
    }

    public static Map<String, ConfigEffect> getEffectConfigs() {
        return ElevatorConfigService.getRootConfig().effects;
    }

    public static Map<String, ElevatorType> getElevatorTypeConfigs() {
        return ElevatorConfigService.getRootConfig().elevators;
    }

    public static boolean isWorldDisabled(World world) {
        return ElevatorConfigService.getRootConfig().disabledWorlds.stream().anyMatch(i -> i.equalsIgnoreCase(world.getName()));
    }

}
