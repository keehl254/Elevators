package me.keehl.elevators.api.models;

import me.keehl.elevators.api.services.interaction.ISimpleDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents a configurable action attached to an {@link IElevatorType}.
 *
 * <p>An action has:
 * <ul>
 *   <li>a server-wide action key ({@link #getKey()}),</li>
 *   <li>a backing elevator type ({@link #getElevatorType()}),</li>
 *   <li>an icon used for UI/editor display ({@link #getIcon()}),</li>
 *   <li>a set of typed variables ({@link #getVariableValue(IElevatorActionVariable)}),</li>
 *   <li>optional editable settings ({@link #getSettings()}), and</li>
 *   <li>execution hooks ({@link #execute(IElevatorEventData, Player)}).</li>
 * </ul>
 *
 * <h3>Lifecycle</h3>
 * <ul>
 *   <li>{@link #initialize(String)} is called once per action instance to parse and apply its configuration string.</li>
 *   <li>After initialization, variables and settings are available and {@link #getIdentifier()} must be non-null.</li>
 * </ul>
 *
 * <h3>Mutability</h3>
 * <ul>
 *   <li>Implementations may be mutable during initialization and when edited via settings/variables.</li>
 *   <li>{@link #getSettings()} should return a snapshot list; modifying it must not affect internal state.</li>
 * </ul>
 */
public interface IElevatorAction {

    /**
     * Sets the icon that represents this action in user interfaces/editors.
     *
     * <p><strong>Contract:</strong> {@code item} must be non-null.</p>
     *
     * @param item non-null icon item
     * @throws NullPointerException if {@code item} is {@code null}
     */
    void setIcon(@NotNull ItemStack item);

    /**
     * Initializes this action from its serialized configuration string.
     *
     * <p>Implementations typically support formats like:
     * {@code key: alias=value alias2=value2 ...} as well as a default variable value when aliases are omitted.</p>
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>{@code value} must be non-null.</li>
     *   <li>This should be called exactly once per action instance before execution/editing APIs are used.</li>
     *   <li>After initialization, {@link #getIdentifier()} must be non-null.</li>
     * </ul>
     *
     * @param value non-null configuration string (may include the {@code key:} prefix)
     * @throws NullPointerException if {@code value} is {@code null}
     */
    void initialize(@NotNull String value);

    /**
     * Returns the elevator type that owns/contains this action.
     *
     * @return the non-null owning elevator type
     */
    @NotNull IElevatorType getElevatorType();

    /**
     * Returns the server-wide action key used to identify this action type.
     *
     * @return the non-null action key
     */
    @Subst("test_key")
    @NotNull String getKey();

    /**
     * Returns the icon that represents this action in user interfaces/editors.
     *
     * @return the non-null icon
     */
    @NotNull ItemStack getIcon();

    /**
     * Serializes this action (including its current variables) into a configuration string.
     *
     * <p><strong>Contract:</strong> Never returns {@code null}.</p>
     *
     * @return a non-null serialized representation of this action
     */
    @NotNull String serialize();

    /**
     * Returns the current value of the given variable using global/default resolution rules.
     *
     * <p>If the variable has not been explicitly set, implementations should return the variable's default value.</p>
     *
     * @param variable non-null variable descriptor
     * @param <T> variable value type
     * @return the resolved value
     * @throws NullPointerException if {@code variable} is {@code null}
     */
    <T> @NotNull T getVariableValue(@NotNull IElevatorActionVariable<T> variable);

    /**
     * Returns the current value of the given variable, optionally using elevator-specific overrides.
     *
     * <p>If {@code elevator} is non-null and the variable is backed by a setting that supports per-elevator values,
     * the returned value may differ per elevator instance.</p>
     *
     * <p>If no value is set, implementations should return the variable's default value.</p>
     *
     * @param variable non-null variable descriptor
     * @param elevator optional elevator context for per-elevator settings; may be {@code null}
     * @param <T> variable value type
     * @return the resolved value
     * @throws NullPointerException if {@code variable} is {@code null}
     */
    <T> @NotNull T getVariableValue(@NotNull IElevatorActionVariable<T> variable, @Nullable IElevator elevator);    /**

     * Sets the value of a variable (also referred to as a grouping) for this action.
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>{@code variable} and {@code value} must be non-null.</li>
     *   <li>Passing a value equal to the variable's default may remove the explicit override.</li>
     * </ul>
     *
     * @param variable non-null variable descriptor
     * @param value new value for the variable
     * @param <T> variable value type
     * @throws NullPointerException if {@code variable} is {@code null}
     */
    <T> void setGroupingObject(@NotNull IElevatorActionVariable<T> variable, @NotNull T value);

    /**
     * Returns the unique identifier for this action instance.
     *
     * <p>This identifier is used to distinguish multiple instances of the same action type.</p>
     *
     * <p><strong>Lifecycle:</strong> After {@link #initialize(String)} completes, this must be non-null.</p>
     *
     * @return the non-null identifier
     */
    @NotNull UUID getIdentifier();

    /**
     * Returns the list of editor settings mapped for this action.
     *
     * <p><strong>Mutability:</strong> The returned list must be a snapshot; modifying it must not affect internal state.</p>
     *
     * @return a non-null list of settings (possibly empty)
     */
    @NotNull List<@NotNull IElevatorActionSetting<?>> getSettings();

    /**
     * Ensures this action has a unique identifier.
     *
     * <p>Implementations may generate and assign a new identifier if one is not present.</p>
     */
    void initIdentifier();

    /**
     * Called when a player begins editing this action.
     *
     * @param player non-null player editing the action
     * @param display non-null display/controller for presenting UI
     * @param elevator non-null elevator context being edited
     */
    void onStartEditing(@NotNull Player player, @NotNull ISimpleDisplay display, @Nullable IElevator elevator);

    /**
     * Called when a player finishes editing this action.
     *
     * @param player non-null player editing the action
     * @param display non-null display/controller for presenting UI
     * @param elevator non-null elevator context being edited
     */
    void onStopEditing(@NotNull Player player, @NotNull ISimpleDisplay display, @Nullable IElevator elevator);

    /**
     * Returns whether this action should execute for the given event/player.
     *
     * <p>This is a pure predicate; it should not have side effects.</p>
     *
     * @param eventData non-null event context
     * @param player non-null player involved in the event
     * @return true if the action should execute; otherwise false
     */
    boolean meetsConditions(@NotNull IElevatorEventData eventData, @NotNull Player player);

    /**
     * Executes the action for the given event/player.
     *
     * <p>Implementations should assume {@link #meetsConditions(IElevatorEventData, Player)} has already been checked
     * unless documented otherwise.</p>
     *
     * @param eventData non-null event context
     * @param player non-null player involved in the event
     */
    void execute(@NotNull IElevatorEventData eventData, @NotNull Player player);

}
