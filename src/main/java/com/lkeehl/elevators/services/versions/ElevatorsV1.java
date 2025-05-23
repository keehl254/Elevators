package com.lkeehl.elevators.services.versions;

import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.ShulkerBoxHelper;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ElevatorTypeService;
import com.lkeehl.elevators.services.ElevatorVersionService;
import com.lkeehl.elevators.services.ElevatorDataContainerService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class ElevatorsV1 extends ElevatorVersionService.ElevatorVersion {

    private ElevatorType getV1ElevatorType(ItemStack item) {
        if (ItemStackHelper.isNotShulkerBox(item.getType()))
            return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return null;

        if (!(meta instanceof BlockStateMeta blockMeta))
            return null;

        if (!(blockMeta.getBlockState() instanceof ShulkerBox box))
            return null;

        ItemStack firstItem = box.getInventory().getItem(0);
        if (firstItem == null)
            return null;

        String type = ElevatorDataContainerService.getElevatorKey(firstItem);
        if (type != null)
            return ElevatorTypeService.getElevatorType(type);

        if (firstItem.getType() != Material.COMMAND_BLOCK)
            return null;

        ItemMeta commandBlockMeta = firstItem.getItemMeta();
        if (commandBlockMeta == null)
            return null;

        if (!commandBlockMeta.hasDisplayName() || !commandBlockMeta.getDisplayName().equalsIgnoreCase("elevator"))
            return null;
        return ElevatorTypeService.getDefaultElevatorType();
    }

    @Override
    public ElevatorType getElevatorType(ItemStack itemStack) {
        if (ItemStackHelper.isNotShulkerBox(itemStack.getType()))
            return null;
        ElevatorType type = getV1ElevatorType(itemStack);
        if (type == null)
            return null;
        ElevatorDataContainerService.updateItemStackFromV2(itemStack, type);

        BlockStateMeta meta = (BlockStateMeta) itemStack.getItemMeta();
        ShulkerBox box = (ShulkerBox) meta.getBlockState();
        box.getSnapshotInventory().clear();
        box.update();
        meta.setBlockState(box);
        itemStack.setItemMeta(meta);

        return type;
    }

    @Override
    public ElevatorType getElevatorType(ShulkerBox box) {
        for (ItemStack item : box.getInventory().getContents()) {
            if (item == null || item.getType().equals(Material.AIR))
                continue;
            if (item.getType().equals(Material.COMMAND_BLOCK) && (item.getItemMeta() != null && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase("elevator")))
                return ElevatorTypeService.getDefaultElevatorType();
            if (item.getType().equals(Material.STONE)) {
                String itemType = ElevatorDataContainerService.getElevatorKey(item);
                if (itemType != null)
                    return ElevatorTypeService.getElevatorType(itemType);
            }
        }
        return null;
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
        box = ElevatorDataContainerService.updateTypeKeyOnElevator(box, ElevatorTypeService.getDefaultElevatorType());
        box = ElevatorDataContainerService.updateBox(box, ElevatorTypeService.getDefaultElevatorType());
        return box;
    }

}
