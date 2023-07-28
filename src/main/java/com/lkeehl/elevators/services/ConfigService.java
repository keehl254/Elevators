package com.lkeehl.elevators.services;

import com.lkeehl.elevators.services.configs.ConfigEffect;
import com.lkeehl.elevators.services.configs.ConfigElevatorType;
import com.lkeehl.elevators.services.configs.ConfigRecipe;
import com.lkeehl.elevators.services.configs.ConfigRoot;
import org.bukkit.World;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigService {

    private static YamlConfigurationLoader loader;
    private static CommentedConfigurationNode rootNode;

    private static final List<Consumer<CommentedConfigurationNode>> configLoadCallbacks = new ArrayList<>();

    public static void loadConfig(File configFile) {
        ConfigService.loader = YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).file(configFile).build();

        try {
            ConfigService.rootNode = loader.load();
            configLoadCallbacks.forEach(i -> i.accept(ConfigService.rootNode));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addConfigCallback(Consumer<CommentedConfigurationNode> callback) {
        ConfigService.configLoadCallbacks.add(callback);
    }

    public static ConfigRoot getRootConfig() {
        try {
            return rootNode.get(ConfigRoot.class);
        } catch (SerializationException e) {
            return new ConfigRoot();
        }
    }

    public static Map<String, ConfigEffect> getEffectConfigs() {
        return getMappedSection(ConfigEffect.class,"effects");
    }

    public static Map<String, ConfigElevatorType> getElevatorTypeConfigs() {
        return getMappedSection(ConfigElevatorType.class,"elevators");
    }

    public static Map<String, ConfigRecipe> getElevatorRecipes(String elevatorKey) {
        return getMappedSection(ConfigRecipe.class, "elevators",elevatorKey,"recipes");
    }

    public static Map<String, String> getElevatorRecipeMaterials(String elevatorKey, String recipeKey) {
        return getMappedSection(String.class, "elevators",elevatorKey,"recipes",recipeKey,"materials");
    }

    public static <T> Map<String, T> getMappedSection(Class<T> clazz, String... path) {
        Map<String, T> map = new HashMap<>();
        if(!rootNode.hasChild((Object[]) path))
            return map;

        Map<Object, CommentedConfigurationNode> children = rootNode.node((Object[]) path).childrenMap();
        for (Object key : children.keySet()) {
            try {
                map.put(key.toString(), children.get(key).get(clazz));
            } catch (ConfigurateException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    public static boolean isWorldDisabled(World world) {
        return ConfigService.getRootConfig().disabledWorlds.stream().anyMatch(i -> i.equalsIgnoreCase(world.getName()));
    }

}
