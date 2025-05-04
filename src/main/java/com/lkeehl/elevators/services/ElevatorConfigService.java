package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ResourceHelper;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.configs.ConfigEffect;
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
import java.util.logging.Level;

public class ElevatorConfigService {

    private static ConfigRootNode<ConfigRoot> rootNode;

    private static ConfigLocale defaultLocaleConfig;

    private static final List<Consumer<ConfigRoot>> configLoadCallbacks = new ArrayList<>();

    public static void loadConfig(File configFile) {
        boolean configCurrentlyExists = configFile.exists();

        try {
            ElevatorConfigService.rootNode = ConfigConverter.createNodeForConfig(new ConfigRoot(), configFile);
            configLoadCallbacks.forEach(i -> i.accept(ElevatorConfigService.rootNode.getConfig()));

            //ConfigService.rootNode.save();
            ConfigConverter.saveConfigToFile(ElevatorConfigService.rootNode, configFile);
        } catch (Exception e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed while loading config. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
    }

    public static void saveConfig(File configFile) {
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
