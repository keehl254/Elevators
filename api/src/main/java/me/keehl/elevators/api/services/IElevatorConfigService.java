package me.keehl.elevators.api.services;

import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.configs.versions.IConfigEffect;
import me.keehl.elevators.api.services.configs.versions.IConfigLocale;
import me.keehl.elevators.api.services.configs.versions.IConfigRoot;
import org.bukkit.World;

import java.util.Map;
import java.util.function.Consumer;

public interface IElevatorConfigService extends IElevatorService {

    void addConfigCallback(Consumer<IConfigRoot> callback);

    IConfigRoot getRootConfig();

    boolean isConfigLoaded();

    IConfigLocale getDefaultLocaleConfig();

    Map<String, IConfigEffect> getEffectConfigs();

    Map<String, IElevatorType> getElevatorTypeConfigs();

    boolean isWorldDisabled(World world);
}
