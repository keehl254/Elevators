package me.keehl.elevators.models.hooks;

import me.keehl.elevators.models.Elevator;

public abstract class HologramHook implements ElevatorHook {

    public abstract WrappedHologram createHologram(Elevator elevator, String... lines);

}
