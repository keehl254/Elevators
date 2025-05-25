package me.keehl.elevators.services.versions;

import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.helpers.ShulkerBoxHelper;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorDataContainerService;
import me.keehl.elevators.services.ElevatorVersionService;
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
        if (!customName.contains(MessageHelper.hideText("CoreEleKey:")))
            return null;

        int sub = customName.indexOf(MessageHelper.hideText("CoreEleKey:"));
        if (sub == -1)
            return null;
        customName = customName.substring(sub);
        String hidden;
        try {
            hidden = MessageHelper.revealText(customName.toLowerCase());
        } catch (Exception e) {
            return null;
        }
        if (!hidden.contains("CoreEleKey:"))
            return null;
        if (hidden.split(":").length == 1)
            return null;
        ElevatorType elevatorType = getClassFromBoxName(hidden.split(":")[1]);
        if(elevatorType != null)
            ElevatorDataContainerService.setElevatorKey(itemStack, elevatorType);
        return elevatorType;
    }

    @Override
    public ElevatorType getElevatorType(ShulkerBox box) {
        if (box.getCustomName() == null)
            return null;
        String customName = box.getCustomName();
        if(!customName.contains(MessageHelper.hideText("CoreEleKey:")))
            return null;

        int sub = customName.indexOf(MessageHelper.hideText("CoreEleKey:"));
        if (sub == -1)
            return null;
        customName = customName.substring(sub);
        String hidden;
        try {
            hidden = MessageHelper.revealText(customName.toLowerCase());
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
        ShulkerBox box = ShulkerBoxHelper.getShulkerBox(block);
        if(box == null)
            return null;
        return getElevatorType(box);
    }

    @Override
    public ShulkerBox convertToLaterVersion(ShulkerBox box) {
        ElevatorType elevatorType = getElevatorType(box);
        box = ElevatorDataContainerService.updateTypeKeyOnElevator(box,elevatorType);
        box = ElevatorDataContainerService.updateBox(box, elevatorType);
        return box;
    }

}
