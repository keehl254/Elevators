package me.keehl.elevators.api.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Describes a typed, aliased variable used to configure an {@link IElevatorAction}.
 *
 * <p>Action variables are typically parsed from an action configuration string (e.g. {@code alias=value})
 * and resolved at runtime to a strongly typed value.</p>
 *
 * <h3>Defaults and invalid input</h3>
 * <ul>
 *   <li>{@link #getDefaultObject()} may be {@code null}.</li>
 *   <li>Implementations may choose to return the default value when the input string is {@code null}
 *       or cannot be parsed.</li>
 * </ul>
 */
public interface IElevatorActionVariable<T> {

    /**
     * Converts a configuration string into a typed variable value.
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>{@code action} must be non-null.</li>
     *   <li>{@code value} may be {@code null}; implementations may treat {@code null} as "use default".</li>
     *   <li>On parse failure, implementations may return {@link #getDefaultObject()}.</li>
     * </ul>
     *
     * @param value the raw string value to parse; may be {@code null}
     * @param action the non-null action this variable belongs to (may be used for context/logging)
     * @return the parsed value, or the default value if {@code value} is {@code null} or invalid
     */
    @NotNull T getObjectFromString(@Nullable String value, @NotNull IElevatorAction action);

    /**
     * Converts a typed value into a string form suitable for serialization.
     *
     * <p><strong>Contract:</strong> {@code object} must be non-null.</p>
     *
     * @param object the non-null value to serialize (typically of type {@code T})
     * @return the non-null string form of the value
     * @throws NullPointerException if {@code object} is {@code null}
     */
    @NotNull String getStringFromObject(@NotNull Object object);

    /**
     * Returns the primary alias used when serializing this variable (e.g., {@code "identifier"}).
     *
     * <p><strong>Contract:</strong> Never {@code null}. Aliases are typically treated case-insensitively.</p>
     *
     * @return the non-null primary alias
     */
    @NotNull String getMainAlias();

    /**
     * Returns the default value for this variable.
     *
     * <p><strong>Contract:</strong> May be {@code null}.</p>
     *
     * @return the default value (possibly {@code null})
     */
    @NotNull T getDefaultObject();

    /**
     * Returns whether the provided alias matches this variable (case-insensitive).
     *
     * <p><strong>Contract:</strong> {@code alias} must be non-null.</p>
     *
     * @param alias non-null alias to test
     * @return true if the alias matches this variable; otherwise false
     * @throws NullPointerException if {@code alias} is {@code null}
     */
    boolean isGroupingAlias(@NotNull String alias);

}
