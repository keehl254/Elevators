package com.lkeehl.elevators.helpers;

import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;

public class ShulkerBoxHelper {

    public static ShulkerBox setShulkerBoxName(ShulkerBox box, String name) {
        box.setCustomName(name);
        box.update(true, true);

        return (ShulkerBox) box.getBlock().getState();
    }

    public static void setFacingUp(ShulkerBox box) {

        if (MCVersionHelper.doesVersionSupportShulkerFacingAPI() && box.getBlockData() instanceof Directional) {
            Directional directionalData = (Directional) box.getBlockData();
            directionalData.setFacing(BlockFace.UP);
            box.setBlockData(directionalData);
        } else {
            box.setRawData((byte) 6);
        }

        box.update(true);

    }

    public static boolean isShulkerBox(BlockState state) {
        return state instanceof ShulkerBox;
    }

    public static ShulkerBox clearContents(ShulkerBox box) {
        box.getInventory().setItem(0, null);
        box.getInventory().setContents(new ItemStack[box.getInventory().getSize()]);
        box.update(true, true);

        return (ShulkerBox) box.getBlock().getState();
    }



}
