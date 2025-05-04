package com.lkeehl.elevators.services.versions;

import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ElevatorVersionService;
import com.lkeehl.elevators.services.ElevatorDataContainerService;
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
        if (ItemStackHelper.isNotShulkerBox(block.getType()))
            return null;
        return getElevatorType((ShulkerBox) block.getState());
    }

    @Override
    public ShulkerBox convertToLaterVersion(ShulkerBox box) {
        return box;
    }

}
