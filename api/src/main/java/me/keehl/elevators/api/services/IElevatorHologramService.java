package me.keehl.elevators.api.services;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.hooks.IWrappedHologram;
import org.bukkit.Chunk;

import java.util.Collection;

public interface IElevatorHologramService extends IElevatorService {

    IWrappedHologram getElevatorHologramIfExists(IElevator elevator);

    IWrappedHologram getElevatorHologram(IElevator elevator);

    void deleteHologram(IElevator elevator);

    void deleteHologramsInChunk(Chunk ignoredChunk);

    void updateHologramsInChunk(Chunk chunk);

    void updateElevatorHologram(IElevator elevator);

    void updateHologramsOfElevatorType(IElevatorType elevatorType);

    Collection<IWrappedHologram> getHolograms();

    IWrappedHologram getHologram(String uuid);

    boolean canUseHolograms();
}
