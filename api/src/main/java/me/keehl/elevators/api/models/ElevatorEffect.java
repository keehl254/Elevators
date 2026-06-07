package me.keehl.elevators.api.models;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Convenience base class for implementing custom {@link IElevatorEffect}s.
 *
 * <p>Third-party developers may extend this class to create custom effects while reusing common helpers such as
 * effect location and dye-based particle color.</p>
 *
 * <h3>Key normalization</h3>
 * <p>The provided {@code effectKey} is normalized to uppercase for consistency. Consumers should treat effect keys as
 * case-insensitive.</p>
 *
 * <h3>Threading</h3>
 * <p>Effects typically interact with the world (particles/sounds). Implementations should assume
 * {@link #playEffect(IElevatorEventData, IElevator)} is called from the server thread. Will run on location
 * threads if in a Folia environment</p>
 */
public abstract class ElevatorEffect implements IElevatorEffect {

    private final String effectKey;

    private final ItemStack icon;

    /**
     * Creates a new effect with a server-wide identifying key and an icon.
     *
     * <p><strong>Contract:</strong>
     * <ul>
     *   <li>{@code effectKey} and {@code icon} must be non-null.</li>
     *   <li>{@code effectKey} is stored in uppercase.</li>
     * </ul>
     *
     * @param effectKey non-null identifying key for this effect
     * @param icon non-null icon used for UI display
     * @throws NullPointerException if any argument is {@code null}
     */
    public ElevatorEffect(@NotNull String effectKey, @NotNull ItemStack icon) {
        Objects.requireNonNull(effectKey);
        Objects.requireNonNull(icon);

        this.effectKey = effectKey.toUpperCase();
        this.icon = icon;
    }

    /**
     * Returns the base location where effects should be played.
     *
     * <p>The default implementation returns a clone of {@link IElevator#getLocation()} to avoid mutating the
     * elevator's underlying location.</p>
     *
     * <p>Subclasses may override to offset the effect location (for example, above the shulker box).</p>
     *
     * @param elevator non-null elevator instance
     * @return non-null effect location
     * @throws NullPointerException if {@code elevator} is {@code null}
     */
    protected @NotNull Location getEffectLocation(@NotNull IElevator elevator) {
        Objects.requireNonNull(elevator);
        return elevator.getLocation().clone();
    }

    private Color extractColorFromDyeColor(DyeColor dyeColor) {
        return dyeColor == null ? Color.WHITE : dyeColor.getColor();
    }

    /**
     * Returns the particle color derived from the elevator's dye color.
     *
     * <p>The default implementation uses {@link IElevator#getDyeColor()} to derive an RGB {@link Color}.</p>
     *
     * @param elevator non-null elevator instance
     * @return non-null particle color
     * @throws NullPointerException if {@code elevator} is {@code null}
     */
    protected @NotNull Color getParticleColor(@NotNull IElevator elevator) {
        Objects.requireNonNull(elevator);
        return this.extractColorFromDyeColor(elevator.getDyeColor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final @NotNull String getEffectKey() {
        return this.effectKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final @NotNull ItemStack getIcon() {
        return this.icon;
    }

    /**
     * Plays this effect for the given event and elevator.
     *
     * <p>Implementations may spawn particles, play sounds, or otherwise interact with the world.</p>
     *
     * @param teleportResult non-null event context describing the elevator interaction/teleport
     * @param elevator non-null elevator instance
     */
    @Override
    public abstract void playEffect(@NotNull IElevatorEventData teleportResult, @NotNull IElevator elevator);

}
