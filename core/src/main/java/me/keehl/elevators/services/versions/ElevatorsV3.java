package me.keehl.elevators.services.versions;

import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.ShulkerBoxHelper;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorVersionService;
import me.keehl.elevators.services.ElevatorDataContainerService;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;

public class ElevatorsV3 extends ElevatorVersionService.ElevatorVersion {

    @Override
    public ElevatorType getElevatorType(ItemStack itemStack) {
        if (ItemStackHelper.isNotShulkerBox(itemStack.getType()))
            return null;
        return getClassFromBoxName(ElevatorDataContainerService.getElevatorKey(itemStack));
    }

    @Override
    public ElevatorType getElevatorType(ShulkerBox box) {
        return getClassFromBoxName(ElevatorDataContainerService.getElevatorKey(box));
    }

    @Override
    public ElevatorType getElevatorType(Block block) {
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
