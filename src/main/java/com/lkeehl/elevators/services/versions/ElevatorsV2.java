package com.lkeehl.elevators.services.versions;

import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ElevatorVersionService;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ElevatorsV2 extends ElevatorVersionService.ElevatorVersion {

    @Override
    public ElevatorType getElevatorType(ItemStack itemStack) {
        if (ItemStackHelper.isNotShulkerBox(itemStack.getType()))
            return null;
        if (!Objects.requireNonNull(itemStack.getItemMeta()).hasDisplayName())
            return null;
        String customName = itemStack.getItemMeta().getDisplayName();
        if (!customName.contains(BaseUtil.hideText("CoreEleKey:")))
            return null;

        int sub = customName.indexOf(BaseUtil.hideText("CoreEleKey:"));
        if (sub == -1)
            return null;
        customName = customName.substring(sub);
        String hidden;
        try {
            hidden = BaseUtil.revealText(customName.toLowerCase());
        } catch (Exception e) {
            return null;
        }
        if (!hidden.contains("CoreEleKey:"))
            return null;
        if (hidden.split(":").length == 1)
            return null;
        ElevatorType elevatorType = getClassFromBoxName(hidden.split(":")[1]);
        if(elevatorType != null)
            BaseElevators.getTag().updateItem(itemStack, elevatorType);
        return elevatorType;
    }

    @Override
    public ElevatorType getElevatorType(ShulkerBox box) {
        if (box.getCustomName() == null)
            return null;
        String customName = box.getCustomName();
        if(!customName.contains(BaseUtil.hideText("CoreEleKey:")))
            return null;

        int sub = customName.indexOf(BaseUtil.hideText("CoreEleKey:"));
        if (sub == -1)
            return null;
        customName = customName.substring(sub);
        String hidden;
        try {
            hidden = BaseUtil.revealText(customName.toLowerCase());
        } catch (Exception e) {
            return null;
        }
        if (!hidden.contains("CoreEleKey:"))
            return null;
        if (hidden.split(":").length == 1)
            return null;
        return getClassFromBoxName(hidden.split(":")[1]);
    }

    @Override
    public ElevatorType getElevatorType(Block block) {
        if (!(block.getState() instanceof ShulkerBox))
            return null;
        return getElevatorType((ShulkerBox) block.getState());
    }

    @Override
    public ShulkerBox convertToLaterVersion(ShulkerBox box) {
        ElevatorType elevatorType = getElevatorType(box);
        box = BaseElevators.getTag().fixPlacedBlock(box, elevatorType);
        box = BaseElevators.getTag().updateBox(box, elevatorType);
        return box;
    }

}
