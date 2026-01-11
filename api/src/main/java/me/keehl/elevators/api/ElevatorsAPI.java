package me.keehl.elevators.api;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.actions.IElevatorActionBuilder;
import me.keehl.elevators.api.models.hooks.IProtectionHook;
import me.keehl.elevators.api.models.settings.IElevatorSettingBuilder;
import me.keehl.elevators.api.services.configs.versions.IConfigHookData;
import me.keehl.elevators.api.util.logging.ILogReleaseData;
import me.keehl.elevators.api.util.persistantDataTypes.ElevatorsDataType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.persistence.PersistentDataType;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ElevatorsAPI {

    public static IElevators getElevators() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(IElevators.class)).orElseThrow();
    }

    public static <T> IElevatorSettingBuilder<T> settingsBuilder(@Pattern("[a-z0-9/._-]+") @Subst("test_key") String settingKey, T defaultValue, PersistentDataType<?, T> persistentDataType) {
        return getElevators().settingsBuilder(settingKey, defaultValue, persistentDataType);
    }

    public static <T> IElevatorSettingBuilder<T> settingsBuilder(@Pattern("[a-z0-9/._-]+") @Subst("test_key") String settingKey, T defaultValue, ElevatorsDataType elevatorsDataType) {
        return getElevators().settingsBuilder(settingKey, defaultValue, elevatorsDataType);
    }

    public static IElevatorActionBuilder actionBuilder(@Pattern("[a-z0-9/._-]+") @Subst("test_key") String actionKey) {
        return getElevators().actionBuilder(actionKey);
    }

    public static IElevator createElevatorRecord(ShulkerBox box, IElevatorType elevatorType) {
        return getElevators().createElevatorRecord(box, elevatorType);
    }

    public static IElevator createElevatorRecord(Block block) {
        return getElevators().createElevatorRecord(block);
    }

    public static IElevatorType getElevatorType(ShulkerBox box) {
        return getElevators().getElevatorType(box);
    }

    public static void toggleElevatorProtectionHook(IElevator elevator, IProtectionHook protectionHook) {
        getElevators().toggleElevatorProtectionHook(elevator, protectionHook);
    }

    public static IConfigHookData getElevatorProtectionHookConfig(IProtectionHook protectionHook) {
        return getElevators().getElevatorProtectionHookConfig(protectionHook);
    }

    public static boolean isElevatorProtectionHookCheckEnabled(IElevator elevator, IProtectionHook protectionHook) {
        return getElevators().isElevatorProtectionHookCheckEnabled(elevator, protectionHook);
    }


    public static void log(Object message) {
        getElevators().log(message);
    }

    public static void log(Level level, Object message) {
        getElevators().log(level, message);
    }

    public static void log(Level level, Object message, Throwable throwable) {
        getElevators().log(level, message, throwable);
    }

    public static void pushLog() {
        getElevators().pushLog();
    }

    public static ILogReleaseData popLog(Consumer<ILogReleaseData> onPop) {
        return getElevators().popLog(onPop);
    }

    public static ILogReleaseData popLog() {
        return getElevators().popLog();
    }

    public static void holdLog() {
        getElevators().holdLog();
    }

    public static void pushAndHoldLog() {
        getElevators().pushAndHoldLog();
    }

    public static ILogReleaseData releaseLog(Consumer<ILogReleaseData> onRelease) {
        return getElevators().releaseLog(onRelease);
    }

    public static ILogReleaseData releaseLog() {
        return getElevators().releaseLog();
    }

    public static Logger getLogger() {
        return getElevators().getLogger();
    }

}
