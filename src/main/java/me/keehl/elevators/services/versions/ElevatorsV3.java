package me.keehl.elevators.services.versions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.ShulkerBoxHelper;
import me.keehl.elevators.services.ElevatorVersionService;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;

public class ElevatorsV3 extends ElevatorVersionService.ElevatorVersion {

    @Override
    public IElevatorType getElevatorType(ItemStack itemStack) {
        if (ItemStackHelper.isNotShulkerBox(itemStack.getType()))
            return null;
        return getClassFromBoxName(Elevators.getDataContainerService().getElevatorKey(itemStack));
    }

    @Override
    public IElevatorType getElevatorType(ShulkerBox box) {
        return getClassFromBoxName(Elevators.getDataContainerService().getElevatorKey(box));
    }

    @Override
    public IElevatorType getElevatorType(Block block) {
        ShulkerBox box = ShulkerBoxHelper.getShulkerBox(block);
        if(box == null)
            return null;
        return getElevatorType(box);
    }

    @Override
    public ShulkerBox convertToLaterVersion(ShulkerBox box) {
        return box;
    }

}
