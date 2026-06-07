package me.keehl.elevators.api.models;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a visual/audio effect that can be played for an elevator interaction.
 *
 * <p>Effects are identified by a server-wide key and may be displayed in user interfaces using an icon.</p>
 *
 * <p><strong>Contract:</strong>
 * <ul>
 *   <li>{@link #getEffectKey()} is never {@code null} or empty.</li>
 *   <li>{@link #getIcon()} is never {@code null}.</li>
 *   <li>{@link #playEffect(IElevatorEventData, IElevator)} may produce world effects (particles, sounds, etc.).</li>
 * </ul>
 */
public interface IElevatorEffect {

    /**
     * Returns the server-wide key that identifies this effect.
     *
     * @return the non-null effect key
     */
    @NotNull String getEffectKey();


    /**
     * Returns the icon used to represent this effect in UIs.
     *
     * @return the non-null icon
     */
    @NotNull ItemStack getIcon();

    /**
     * Plays this effect for the given event and elevator.
     *
     * <p>This method may spawn particles, play sounds, or otherwise interact with the world.</p>
     *
     * @param teleportResult non-null event context describing the elevator interaction/teleport
     * @param elevator non-null elevator instance
     * @throws NullPointerException if any argument is {@code null}
     */
    void playEffect(@NotNull IElevatorEventData teleportResult, @NotNull IElevator elevator);

}
