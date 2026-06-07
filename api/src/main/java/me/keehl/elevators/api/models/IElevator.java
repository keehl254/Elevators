package me.keehl.elevators.api.models;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a lightweight, immutable view of an elevator instance at a specific location.
 *
 * <p>An {@code IElevator} contains:
 * <ul>
 *   <li>the backing {@link ShulkerBox} block state,</li>
 *   <li>a snapshot {@link IElevatorType} reference,</li>
 *   <li>derived properties such as {@link Location} and {@link DyeColor}.</li>
 * </ul>
 *
 * <p>This object does not perform elevator logic on its own; it is intended to be passed into other API methods
 * that require both elevator state and an elevator type.</p>
 *
 * <p><strong>Immutability:</strong> Implementations are expected to be immutable with respect to their stored references,
 * but their derived values (location, block state, live type) may change as the world and registrations change.</p>
 */
public interface IElevator {

    /**
     * Returns the backing shulker box block state for this elevator.
     *
     * <p><strong>Contract:</strong> Never {@code null}.</p>
     *
     * @return the shulker box backing this elevator
     */
    @NotNull ShulkerBox getShulkerBox();

    /**
     * Returns the snapshot elevator type reference stored in this elevator record.
     *
     * <p>This is the elevator type that was associated with this elevator when it was
     * originally resolved or constructed. It does not change over the lifetime of
     * this {@code IElevator} instance. If a reload occurs or the elevator type is
     * deleted, this snapshot will become invalid.</p>
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>Never returns {@code null}.</li>
     *   <li>The returned type may no longer be registered or may be a replaced instance.</li>
     * </ul>
     *
     * @return the non-null snapshot elevator type reference
     */
    @NotNull IElevatorType getSnapshotElevatorType();

    /**
     * Attempts to resolve the currently registered elevator type for this elevator.
     *
     * <p>This method performs a live lookup using the snapshot type's key and returns
     * the currently registered {@link IElevatorType}, if one exists.</p>
     *
     * <p>If the elevator type has been unregistered or replaced since this elevator
     * was created, the returned {@link Optional} will be empty.</p>
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>Never returns {@code null}.</li>
     *   <li>The returned {@link Optional} is empty if no matching elevator type is currently registered.</li>
     * </ul>
     *
     * @return an {@link Optional} containing the currently registered elevator type,
     *         or an empty {@link Optional} if none is registered
     */
    @NotNull Optional<IElevatorType> resolveElevatorTypeLive();

    /**
     * Returns the location of the backing shulker box in the world.
     *
     * <p><strong>Contract:</strong> For elevators resolved from placed blocks, this should be non-null.</p>
     *
     * @return the elevator's location
     */
    @NotNull Location getLocation();

    /**
     * Returns the dye color of the backing shulker box.
     *
     * <p><strong>Invariant:</strong> Elevators cannot be created from undyed shulker boxes, so this is never {@code null}.</p>
     *
     * @return the non-null dye color of this elevator
     */
    @NotNull DyeColor getDyeColor();

    /**
     * Returns whether this elevator record still matches the current world state and registered type.
     *
     * <p>Implementations typically check that:
     * <ul>
     *   <li>the block at {@link #getLocation()} is still the same shulker box type, and</li>
     *   <li>the snapshot type is still the currently registered type for its key.</li>
     * </ul>
     *
     * <p><strong>Contract:</strong> Returns {@code false} rather than throwing if the elevator can no longer be resolved live.</p>
     *
     * @return {@code true} if the elevator still appears valid; otherwise {@code false}
     */
    boolean isValid();

}
