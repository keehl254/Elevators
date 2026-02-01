package me.keehl.elevators.api;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.actions.IElevatorActionBuilder;
import me.keehl.elevators.api.models.settings.IElevatorSettingBuilder;
import me.keehl.elevators.api.util.logging.ILogReleaseData;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.persistence.PersistentDataType;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ElevatorsAPI {

    /**
     * Returns the registered {@link IElevators} service from Bukkit's {@link org.bukkit.plugin.ServicesManager}.
     *
     * @return the registered elevators service
     * @throws IllegalStateException if no {@link IElevators} service is registered
     */
    public static @NotNull IElevators getElevators() {
        final IElevators service = Bukkit.getServicesManager().load(IElevators.class);
        if (service == null)
            throw new IllegalStateException("IElevators service is not registered.");

        return service;
    }

    /**
     * Creates a builder for defining a new elevator setting.
     *
     * <p>This is intended for third-party developers to add custom settings that Elevators can persist and
     * apply to both individual elevators and elevator types.</p>
     *
     * <p><strong>Registration:</strong>
     * Keys are server-wide. Calling the builder's {@code register(...)} method with a key
     * that is already registered, or with an invalid plugin instance, throws {@link IllegalStateException}.</p>
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>{@code settingKey}, {@code defaultValue}, and {@code persistentDataType} must be non-null.</li>
     *   <li>{@code settingKey} must match {@code [a-z0-9/._-]+}.</li>
     * </ul>
     *
     * @param settingKey unique, lowercase identifier for the setting (pattern: {@code [a-z0-9/._-]+})
     * @param defaultValue the non-null default value used when the setting is not present
     * @param persistentDataType the Bukkit persistent data type used to serialize/deserialize values
     * @param <T> the value type of the setting
     * @return a builder used to further configure and register the setting
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static <T> @NotNull IElevatorSettingBuilder<T> settingsBuilder(@NotNull @Pattern("[a-z0-9/._-]+") @Subst("test_key") String settingKey, @NotNull T defaultValue, @NotNull PersistentDataType<?, T> persistentDataType) {
        Objects.requireNonNull(settingKey, "settingKey");
        Objects.requireNonNull(defaultValue, "defaultValue");
        Objects.requireNonNull(persistentDataType, "persistentDataType");
        return getElevators().settingsBuilder(settingKey, defaultValue, persistentDataType);
    }

    /**
     * Creates a builder for defining a new elevator action.
     *
     * <p>Actions are executed by elevators when a player uses an elevator (on travel),
     * allowing third-party developers to inject custom behavior.</p>
     *
     * <p><strong>Registration:</strong>
     * Keys are server-wide. Calling the builder's {@code register(...)} method with a key
     * that is already registered, or with an invalid plugin instance, throws {@link IllegalStateException}.</p>
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>{@code actionKey} must be non-null.</li>
     *   <li>{@code actionKey} must match {@code [a-z0-9/._-]+}.</li>
     * </ul>
     *
     * @param actionKey unique, lowercase identifier for the action (pattern: {@code [a-z0-9/._-]+})
     * @return a builder used to configure and register the action
     * @throws NullPointerException if {@code actionKey} is {@code null}
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static @NotNull IElevatorActionBuilder actionBuilder(@NotNull @Pattern("[a-z0-9/._-]+") @Subst("test_key") String actionKey) {
        Objects.requireNonNull(actionKey, "actionKey");
        return getElevators().actionBuilder(actionKey);
    }

    /**
     * Creates a lightweight {@link IElevator} data record for the given shulker box and elevator type.
     *
     * <p>{@link IElevator} is a simple representation of an elevator instance (location, dye color, type, backing
     * shulker box) and does not perform logic by itself. It is intended to be passed into other API methods that
     * require both state and a resolved elevator type.</p>
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>{@code box}, {@code elevatorType} are never {@code null}.</li>
     *   <li>This method does not make modifications.</li>
     * </ul>
     *
     * @param box the placed shulker box backing a potential elevator
     * @param elevatorType the elevator type definition to associate with the returned record
     * @return an {@link IElevator} record
     *
     * @throws NullPointerException if {@code box} or {@code elevatorType} is {@code null}
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static @NotNull IElevator resolveElevator(@NotNull ShulkerBox box,  @NotNull IElevatorType elevatorType) {
        Objects.requireNonNull(box, "box");
        Objects.requireNonNull(elevatorType, "elevatorType");
        return getElevators().resolveElevator(box, elevatorType);
    }

    /**
     * Creates a lightweight {@link IElevator} data record from a world {@link Block}.
     *
     * <p>{@link IElevator} is a simple representation of an elevator instance and does not perform logic by itself.</p>
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>{@code block} must be non-null.</li>
     *   <li>Returns {@code null} if {@code block} is not a shulker box or if the block does not represent an elevator.</li>
     *   <li>This method does not make modifications.</li>
     * </ul>
     *
     * @param block the block to inspect
     * @return an {@link IElevator} record, or {@code null} if the block is not a shulker box elevator
     *
     * @throws NullPointerException if {@code block} is {@code null}
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static @Nullable IElevator resolveElevator(@NotNull Block block) {
        Objects.requireNonNull(block, "block");
        return getElevators().resolveElevator(block);
    }

    /**
     * Resolves the {@link IElevatorType} associated with the given shulker box.
     *
     * <p>This method performs a lookup based on the provided {@link ShulkerBox} state and returns the matching
     * elevator type if the shulker box represents a valid elevator.</p>
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>{@code box} must be non-null.</li>
     *   <li>Returns {@code null} if {@code box} does not represent a valid elevator shulker box or no type is associated.</li>
     * </ul>
     *
     * @param box the shulker box state to inspect
     * @return the resolved elevator type, or {@code null} if the shulker box is not a valid elevator or no type is present
     *
     * @throws NullPointerException if {@code box} is {@code null}
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static @Nullable IElevatorType resolveElevatorType(@NotNull ShulkerBox box) {
        Objects.requireNonNull(box, "box");
        return getElevators().resolveElevatorType(box);
    }

    /**
     * Logs a message at the default level using Elevators' structured logger.
     *
     * @param message non-null message object (converted to a string by the logger)
     * @throws NullPointerException if {@code message} is {@code null}
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static void log(@NotNull Object message) {
        Objects.requireNonNull(message, "message");
        getElevators().log(message);
    }

    /**
     * Logs a message at the given level using Elevators' structured logger.
     *
     * @param level non-null log level
     * @param message non-null message object (converted to a string by the logger)
     * @throws NullPointerException if {@code level} or {@code message} is {@code null}
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static void log(@NotNull Level level, @NotNull Object message) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(message, "message");
        getElevators().log(level, message);
    }

    /**
     * Logs a message at the given level with an associated throwable.
     *
     * @param level non-null log level
     * @param message non-null message object (converted to a string by the logger)
     * @param throwable non-null throwable to log
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static void log(@NotNull Level level, @NotNull Object message, @NotNull Throwable throwable) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(throwable, "throwable");
        getElevators().log(level, message, throwable);
    }

    /**
     * Pushes a new indentation frame onto the log stack.
     *
     * <p>After pushing, subsequent log output is indented one additional level until {@link #popLog()} is called.
     * Nested pushes increase indentation further.</p>
     *
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static void pushLog() {
        getElevators().pushLog();
    }

    /**
     * Pops the current indentation frame from the log stack and returns release metadata, running a callback
     * before any buffered log lines are printed.
     *
     * <p>This is useful for logging a summary header (e.g., duration) before emitting the buffered body.</p>
     *
     * <p>If the current frame is being held, calling this method will also release the buffered logs.</p>
     *
     * If there is no current frame/held output, this returns an empty release data (empty list, elapsed time = 0).
     *
     * @param onPop non-null callback invoked with the release data prior to printing buffered logs
     * @return non-null release data for the popped frame
     * @throws NullPointerException if {@code onPop} is {@code null}
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static @NotNull ILogReleaseData popLog(@NotNull Consumer<@NotNull ILogReleaseData> onPop) {
        Objects.requireNonNull(onPop, "onPop");
        return getElevators().popLog(onPop);
    }

    /**
     * Pops the current indentation frame from the log stack and returns release metadata.
     *
     * <p>If the current frame is being held (see {@link #holdLog()}), calling this method will also release the
     * buffered logs as part of the pop operation.</p>
     *
     * <p>The returned {@link ILogReleaseData} contains the buffered log lines for the popped frame (if any) and the
     * elapsed time in milliseconds between the corresponding push and this pop.</p>
     *
     * If there is no current frame/held output, this returns an empty release data (empty list, elapsed time = 0).
     *
     * @return non-null release data for the popped frame
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static @NotNull ILogReleaseData popLog() {
        return getElevators().popLog();
    }

    /**
     * Starts buffering subsequent log output instead of printing immediately.
     *
     * <p>Buffered output is printed when {@link #releaseLog()} is called (and will respect the current indentation).</p>
     *
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static void holdLog() {
        getElevators().holdLog();
    }

    /**
     * Convenience method equivalent to calling {@link #pushLog()} and then {@link #holdLog()}.
     * Subsequent logs are both indented and buffered until released or popped.</p>
     *
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static void pushAndHoldLog() {
        getElevators().pushAndHoldLog();
    }

    /**
     * Releases buffered logs, running a callback before printing, and returns release metadata.
     *
     * <p>This overload is useful for printing a header line (e.g., "completed in Xms") before the buffered body.</p>
     *
     * If there is no current frame/held output, this returns an empty release data (empty list, elapsed time = 0).
     *
     * @param onRelease non-null callback invoked with the release data prior to printing buffered logs
     * @return non-null release data containing buffered logs and elapsed time
     * @throws NullPointerException if {@code onRelease} is {@code null}
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static @NotNull ILogReleaseData releaseLog(@NotNull Consumer<@NotNull ILogReleaseData> onRelease) {
        Objects.requireNonNull(onRelease, "onRelease");
        return getElevators().releaseLog(onRelease);
    }

    /**
     * Releases buffered logs, printing them to the console, and returns release metadata.
     *
     * <p>The returned {@link ILogReleaseData} contains the buffered log lines and the elapsed time in milliseconds
     * since the corresponding push/hold context began.</p>
     *
     * If there is no current frame/held output, this returns an empty release data (empty list, elapsed time = 0).
     *
     * @return non-null release data containing buffered logs and elapsed time
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static @NotNull ILogReleaseData releaseLog() {
        return getElevators().releaseLog();
    }

    /**
     * Returns the underlying {@link Logger} used by the Elevators logging system.
     *
     * @return the non-null logger instance
     * @throws IllegalStateException if the Elevators service is not registered
     */
    public static @NotNull Logger getLogger() {
        return getElevators().getLogger();
    }

}
