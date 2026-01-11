package me.keehl.elevators.api.services;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface IElevatorObstructionService extends IElevatorService {

    double getHitBoxAddition(Block block, Player player);
}
