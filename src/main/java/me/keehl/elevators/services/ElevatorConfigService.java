package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.IElevatorConfigService;
import me.keehl.elevators.api.services.configs.versions.IConfigEffect;
import me.keehl.elevators.api.services.configs.versions.IConfigRoot;
import me.keehl.elevators.services.configs.ConfigVersionBuilder;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigLocale;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigRoot;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ConfigRootNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ElevatorConfigService extends ElevatorService implements IElevatorConfigService {

    private boolean invalidConfig = false;

    private ConfigRootNode<ConfigRoot> rootNode;

    private ConfigLocale defaultLocaleConfig;

    private final List<Consumer<IConfigRoot>> configLoadCallbacks = new ArrayList<>();

    public ElevatorConfigService(IElevators elevators) {
        super(elevators);
    }

    @Override
    public void onInitialize() {

    }

    @Override
    public void onUninitialize() {

    }

    public void loadConfig(File configFile) {
        ElevatorsAPI.pushAndHoldLog();

        this.rootNode = ConfigVersionBuilder.getConfig(configFile);
        if(this.rootNode == null) {
            Bukkit.getPluginManager().disablePlugin(Elevators.getInstance());
            return;
        }

        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Config loaded. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

        this.configLoadCallbacks.forEach(i -> i.accept(this.rootNode.getConfig()));
        ConfigConverter.saveConfigToFile(this.rootNode, configFile);
    }

    public void invalidateConfig() {
        this.invalidConfig = true;
    }

    public void saveConfig(File configFile) {
        if(this.invalidConfig)
            return;

        ConfigConverter.saveConfigToFile(this.rootNode, configFile);
    }

    @Override
    public void addConfigCallback(Consumer<IConfigRoot> callback) {
        this.configLoadCallbacks.add(callback);
        if(this.rootNode != null)
            callback.accept(this.rootNode.getConfig());
    }

    @Override
    public IConfigRoot getRootConfig() {
        return this.rootNode.getConfig();
    }

    @Override
    public boolean isConfigLoaded() {
        return this.rootNode != null;
    }

    @Override
    public ConfigLocale getDefaultLocaleConfig() {
        if(this.defaultLocaleConfig == null)
            this.defaultLocaleConfig = new ConfigLocale();

        return this.defaultLocaleConfig;
    }

    @Override
    public Map<String, IConfigEffect> getEffectConfigs() {
        return this.getRootConfig().getEffects();
    }

    @Override
    public Map<String, IElevatorType> getElevatorTypeConfigs() {
        return this.getRootConfig().getElevators();
    }

    @Override
    public boolean isWorldDisabled(World world) {
        return this.getRootConfig().getDisabledWorlds().stream().anyMatch(i -> i.equalsIgnoreCase(world.getName()));
    }
}
