package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ResourceHelper;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.configs.BlankRoot;
import com.lkeehl.elevators.services.configs.ConfigVersionBuilder;
import com.lkeehl.elevators.services.configs.versions.configv1.V1ConfigRoot;
import com.lkeehl.elevators.services.configs.versions.configv2.V2ConfigRoot;
import com.lkeehl.elevators.services.configs.versions.configv5.ConfigEffect;
import com.lkeehl.elevators.services.configs.versions.configv5.ConfigLocale;
import com.lkeehl.elevators.services.configs.versions.configv5.ConfigRoot;
import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ConfigRootNode;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ElevatorConfigService {

    private static boolean invalidConfig = false;

    private static ConfigRootNode<ConfigRoot> rootNode;

    private static ConfigLocale defaultLocaleConfig;

    private static final List<Consumer<ConfigRoot>> configLoadCallbacks = new ArrayList<>();

    public static void loadConfig(File configFile) {
        boolean configCurrentlyExists = configFile.exists();

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
