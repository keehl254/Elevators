package me.keehl.elevators.api.models.hooks;

import me.keehl.elevators.api.models.IElevator;

import java.util.UUID;

public interface HologramHook extends ElevatorHook {

    IElevatorHologram createHologram(UUID uuid, IElevator elevator, String... lines);

}
