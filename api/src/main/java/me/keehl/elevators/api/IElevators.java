package me.keehl.elevators.api;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.models.actions.IElevatorActionBuilder;
import me.keehl.elevators.api.models.hooks.IProtectionHook;
import me.keehl.elevators.api.models.settings.IElevatorSettingBuilder;
import me.keehl.elevators.api.services.configs.versions.IConfigHookData;
import me.keehl.elevators.api.util.logging.ILogReleaseData;
import me.keehl.elevators.api.util.persistantDataTypes.ElevatorsDataType;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Pattern;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface IElevators {

    JavaPlugin getPlugin();

    void log(Object message);

    void log(Level level, Object message);

    void log(Level level, Object message, Throwable throwable);

    void pushLog();

    ILogReleaseData popLog(Consumer<ILogReleaseData> onPop);

    ILogReleaseData popLog();

    void holdLog();

    void pushAndHoldLog();

    ILogReleaseData releaseLog(Consumer<ILogReleaseData> onRelease);

    ILogReleaseData releaseLog();

    Logger getLogger();

    ILocaleComponent createComponentFromText(String message);

    <T> IElevatorSettingBuilder<T> settingsBuilder(@Pattern("[a-z0-9/._-]+") String settingKey, T defaultValue, PersistentDataType<?, T> persistentDataType);

    <T> IElevatorSettingBuilder<T> settingsBuilder(@Pattern("[a-z0-9/._-]+") String settingKey, T defaultValue, ElevatorsDataType elevatorsDataType);

    IElevatorActionBuilder actionBuilder(String actionKey);

    IElevator createElevatorRecord(ShulkerBox box, IElevatorType elevatorType);

    IElevator createElevatorRecord(Block block);

    IElevatorType getElevatorType(ShulkerBox box);

    void toggleElevatorProtectionHook(IElevator elevator, IProtectionHook protectionHook);

    IConfigHookData getElevatorProtectionHookConfig(IProtectionHook protectionHook);

    boolean isElevatorProtectionHookCheckEnabled(IElevator elevator, IProtectionHook protectionHook);

}
