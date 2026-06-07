package me.keehl.elevators.api.models;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a configurable setting that may apply globally to an {@link IElevatorType}
 * and optionally be overridden per {@link IElevator}.
 *
 * <h3>Global vs individual values</h3>
 * <ul>
 *   <li><strong>Global value:</strong> derived from the owning {@link IElevatorType} (see {@link #getGlobalValue(IElevatorType)}).</li>
 *   <li><strong>Individual value:</strong> when supported, stored on the elevator's backing block via a persistent datastore
 *       and read via {@link #getIndividualValue(IElevator)}.</li>
 * </ul>
 *
 * <p><strong>Global-only behavior:</strong> A setting is treated as global-only when either:
 * <ul>
 *   <li>the elevator type disables the setting by name via config, or</li>
 *   <li>{@link #canBeEditedIndividually(IElevator)} returns false.</li>
 * </ul>
 *
 * @param <T> the value type for this setting (e.g., Boolean, String, Integer)
 */
public interface IElevatorSetting<T> {

    /**
     * Adds an informational UI action line to this setting's icon/lore.
     *
     * <p>This does not change the setting value; it only affects the generated icon lore.</p>
     *
     * @param action non-null action label (e.g., "LEFT-CLICK")
     * @param description non-null action description
     * @return this setting instance (for chaining)
     * @throws NullPointerException if any argument is {@code null}
     */
    @NotNull IElevatorSetting<T> addAction(@NotNull String action, @NotNull String description);

    /**
     * Returns whether this setting is forced to be global-only for the given elevator.
     *
     * <p>This is typically true when the elevator type has disabled this setting by name or when individual editing
     * is not supported for the elevator/context.</p>
     *
     * @param elevator non-null elevator context
     * @return true if individual overrides are not allowed; otherwise false
     * @throws NullPointerException if {@code elevator} is {@code null}
     */
    boolean isSettingGlobalOnly(@NotNull IElevator elevator);

    /**
     * Returns whether this setting supports per-elevator (individual) editing for the given elevator.
     *
     * <p>If this returns false, the setting is effectively global-only for that elevator.</p>
     *
     * @param elevator non-null elevator context
     * @return true if per-elevator editing is supported; otherwise false
     * @throws NullPointerException if {@code elevator} is {@code null}
     */
    boolean canBeEditedIndividually(@NotNull IElevator elevator);

    /**
     * Creates an icon representing this setting and its current value for UI display.
     *
     * <p><strong>Contract:</strong> {@code value} must be non-null.</p>
     *
     * @param value non-null current value (global or individual)
     * @param global whether the icon represents the global value or an individual value
     * @return a non-null item stack icon (typically a clone of an internal template)
     * @throws NullPointerException if {@code value} is {@code null}
     */
    @NotNull ItemStack createIcon(@NotNull Object value, boolean global);

    /**
     * Handles a click on this setting in a "global" context.
     *
     * <p>This convenience method typically calls {@link #getGlobalValue(IElevatorType)} and then delegates to
     * {@link #onClickGlobal(Player, IElevatorType, Runnable, InventoryClickEvent, Object)}.</p>
     *
     * @param player non-null clicking player
     * @param elevatorType non-null elevator type being edited
     * @param returnMethod non-null callback to return to the previous UI
     * @param clickEvent non-null inventory click event
     */
    void clickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent);

    /**
     * Handles a click on this setting in an "individual" context.
     *
     * <p>This convenience method typically calls {@link #getIndividualValue(IElevator)} and then delegates to
     * {@link #onClickIndividual(Player, IElevator, Runnable, InventoryClickEvent, Object)}.</p>
     *
     * @param player non-null clicking player
     * @param elevator non-null elevator being edited
     * @param returnMethod non-null callback to return to the previous UI
     * @param clickEvent non-null inventory click event
     */
    void clickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent);

    /**
     * Called when the setting is clicked in a global context, with the current global value already provided.
     *
     * @param player non-null clicking player
     * @param elevatorType non-null elevator type being edited
     * @param returnMethod non-null callback to return to the previous UI
     * @param clickEvent non-null inventory click event
     * @param currentValue current global value
     */
    void onClickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull T currentValue);

    /**
     * Called when the setting is clicked in an individual context, with the current individual value already provided.
     *
     * @param player non-null clicking player
     * @param elevator non-null elevator being edited
     * @param returnMethod non-null callback to return to the previous UI
     * @param clickEvent non-null inventory click event
     * @param currentValue current individual value
     */
    void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull T currentValue);

    /**
     * Returns the global value of this setting for the given elevator type.
     *
     * @param elevatorType non-null elevator type context
     * @return the global value (implementations should not return {@code null} unless explicitly documented)
     */
    @NotNull T getGlobalValue(@NotNull IElevatorType elevatorType);

    /**
     * Returns the resolved value of this setting for a specific elevator.
     *
     * <p>If individual editing is supported and a datastore is configured, implementations typically read the stored
     * override from the elevator's persistent data container; otherwise the global value is returned.</p>
     *
     * @param elevator non-null elevator context
     * @return the resolved value (typically non-null)
     */
    @NotNull T getIndividualValue(@NotNull IElevator elevator);

    /**
     * Sets the per-elevator override value for this setting.
     *
     * <p><strong>Side effects:</strong> Implementations will write to the elevator's persistent data container and update
     * the backing block state.</p>
     *
     * <p><strong>Optimization behavior:</strong> Implementations may remove the stored override when {@code value}
     * equals the current global value.</p>
     *
     * @param elevator non-null elevator to modify
     * @param value new value to store (may be treated as "remove override" when equal to global)
     * @throws RuntimeException if this setting does not support individual overrides (e.g., datastore not configured)
     */
    void setIndividualValue(@NotNull IElevator elevator, @NotNull T value);

    /**
     * Returns the internal setting name/key (server-wide).
     *
     * @return the non-null setting name
     */
    @NotNull String getSettingName();

    /**
     * Returns the plugin that provided/owns this setting.
     *
     * @return the non-null plugin
     */
    @NotNull JavaPlugin getPlugin();

}
