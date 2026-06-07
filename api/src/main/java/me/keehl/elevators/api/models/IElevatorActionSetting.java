package me.keehl.elevators.api.models;

import me.keehl.elevators.api.util.PentaConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Represents an editable setting for an {@link IElevatorAction} variable.
 *
 * <p>This setting bridges a string-backed datastore (via {@link IElevatorSetting}) with a typed value {@code T}
 * used during editing. Implementations typically:
 * <ul>
 *   <li>parse the current string value into {@code T},</li>
 *   <li>open a UI/editor to choose a new {@code T},</li>
 *   <li>apply the chosen value either globally (action variable) or per-elevator (datastore override).</li>
 * </ul>
 *
 * <h3>Click handling</h3>
 * <p>Before invoking {@link #onClickGlobal(Player, IElevatorType, Runnable, InventoryClickEvent, String)} or
 * {@link #onClickIndividual(Player, IElevator, Runnable, InventoryClickEvent, String)}, an onClick handler must be
 * registered using {@link #onClick(PentaConsumer)}.</p>
 */
public interface IElevatorActionSetting<T> extends IElevatorSetting<String> {

    /**
     * Registers the click handler used to edit this setting.
     *
     * <p>The handler is provided:
     * <ul>
     *   <li>the player,</li>
     *   <li>a return callback to go back to the previous UI,</li>
     *   <li>the click event,</li>
     *   <li>the current parsed value ({@code T}),</li>
     *   <li>a consumer that must be called with the new value to persist/apply it.</li>
     * </ul>
     *
     * <p><strong>Contract:</strong> Must be called exactly once during setup before any click methods are used.</p>
     *
     * @param setValueGlobalMethod non-null click handler
     * @throws NullPointerException if {@code setValueGlobalMethod} is {@code null}
     */
    void onClick(@NotNull PentaConsumer<@NotNull Player, @NotNull Runnable, @NotNull InventoryClickEvent, @NotNull T, @NotNull Consumer<T>> setValueGlobalMethod);

    /**
     * Invoked when this setting is clicked in the admin menu (applies to the action's global variable value).
     *
     * <p>The implementation should parse {@code currentValue} into {@code T}, then delegate to the registered
     * onClick handler.</p>
     *
     * @param player non-null clicking player
     * @param elevatorType non-null elevator type being edited
     * @param returnMethod non-null callback to return to the previous UI
     * @param clickEvent non-null inventory click event
     * @param currentValue non-null current string value for this setting
     * @throws IllegalStateException if no onClick handler was registered via {@link #onClick(PentaConsumer)}
     */
    void onClickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull String currentValue);

    /**
     * Invoked when this setting is clicked in an "individual" context (applies per elevator instance).
     *
     * <p>This is only called when {@link #canBeEditedIndividually(IElevator)} is true.</p>
     *
     * @param player non-null clicking player
     * @param elevator non-null elevator being edited
     * @param returnMethod non-null callback to return to the previous UI
     * @param clickEvent non-null inventory click event
     * @param currentValue non-null current string value for this setting
     * @throws IllegalStateException if no onClick handler was registered via {@link #onClick(PentaConsumer)}
     */
    void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull String currentValue);

    /**
     * Returns the current global value for this setting as a string.
     *
     * <p>This is typically derived by serializing the action variable value.</p>
     *
     * @param elevatorType non-null elevator type context
     * @return non-null current global value in string form
     * @throws NullPointerException if {@code elevatorType} is {@code null}
     */
    @NotNull String getGlobalValue(@NotNull IElevatorType elevatorType);

    /**
     * Configures persistent storage for per-elevator values of this setting.
     *
     * <p>Implementations may derive the actual storage key from {@code settingKey} (for example by prefixing with
     * an action instance identifier).</p>
     *
     * @param settingKey non-null base key for persistent storage
     * @param dataType non-null data type used for storage
     * @return the underlying (string) setting that was configured for storage
     * @throws NullPointerException if any argument is {@code null}
     */
    @NotNull IElevatorSetting<String> setupDataStore(@NotNull String settingKey, @NotNull PersistentDataType<?, String> dataType);

    /**
     * Returns whether this setting can be edited per-elevator (i.e., supports individual overrides).
     *
     * @param elevator non-null elevator context
     * @return true if individual editing is supported; otherwise false
     * @throws NullPointerException if {@code elevator} is {@code null}
     */
    boolean canBeEditedIndividually(@NotNull IElevator elevator);


}
