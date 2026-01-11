package me.keehl.elevators.api.services;

import me.keehl.elevators.api.models.IElevatorType;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;

public interface IElevatorVersionService extends IElevatorService {

    IElevatorType getElevatorType(ItemStack item);

    IElevatorType getElevatorType(ShulkerBox box, boolean updateBlock);

    IElevatorType getElevatorType(Block block);
}
