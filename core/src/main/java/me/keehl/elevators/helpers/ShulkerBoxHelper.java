package me.keehl.elevators.helpers;

import io.github.rvskele.paperlib.PaperLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class ShulkerBoxHelper {

    private static final Random random = new Random();

    public static ShulkerBox setShulkerBoxName(ShulkerBox box, String name) {
        box.setCustomName(name);
        box.update(true, true);

        return ShulkerBoxHelper.getShulkerBox(box.getBlock());
    }

    @SuppressWarnings("deprecation")
    public static void setFacingUp(ShulkerBox box) {
        BlockData blockData = box.getBlockData();
        if (VersionHelper.doesVersionSupportShulkerFacingAPI() && blockData instanceof Directional) {
            ((Directional) blockData).setFacing(BlockFace.UP);
            box.setBlockData(blockData);
        } else {
            box.setRawData((byte) 6);
        }

        box.update(true);

    }

    public static boolean isShulkerBox(BlockState state) {
        return state instanceof ShulkerBox;
    }

    public static ShulkerBox getShulkerBox(Block block) {
        if (ItemStackHelper.isNotShulkerBox(block.getType()))
            return null;

        return (ShulkerBox) PaperLib.getBlockState(block, false).getState();
    }

    public static ShulkerBox clearContents(ShulkerBox box) {
        box.getInventory().setItem(0, null);
        box.getInventory().setContents(new ItemStack[box.getInventory().getSize()]);
        box.update(true, true);

        return ShulkerBoxHelper.getShulkerBox(box.getBlock());
    }

    public static void playClose(ShulkerBox box) {
        VersionHelper.closeShulkerBox(box);
    }

    public static void playOpen(ShulkerBox box) {
        VersionHelper.openShulkerBox(box);
    }

    public static boolean fakeDispense(Block block, ItemStack item) {
        if (block.getType() != Material.DISPENSER)
            return false;
        Dispenser dispenser = (Dispenser) PaperLib.getBlockState(block, false).getState();

        Directional directional = (Directional) block.getBlockData();

        boolean match = false;
        for (ItemStack tempItem : dispenser.getInventory().getContents()) {
            if (tempItem != null && tempItem.isSimilar(item)) {
                match = true;
                tempItem.setAmount(tempItem.getAmount() - 1);
                break;
            }
        }

        if (!match)
            return false;

        item = item.clone();
        item.setAmount(1);

        BlockFace face = directional.getFacing();
        double d0 = block.getX() + (0.7 * (double) face.getModX());
        double d1 = block.getY() + (0.7 * (double) face.getModY());
        double d2 = block.getZ() + (0.7 * (double) face.getModZ());

        if (face == BlockFace.UP || face == BlockFace.DOWN)
            d1 -= 0.125;
        else
            d1 -= 0.15625;
        Location spawnLocation = new Location(block.getWorld(), d0, d1, d2);

        double power = 6;
        VersionHelper.dropItem(block.getWorld(), spawnLocation, item, newItem -> {
            double d3 = random.nextDouble() * 0.1 + 0.2;

            double velX = random.nextGaussian() * 0.007499999832361937 * power + (double) face.getModX() * d3;
            double velY = random.nextGaussian() * 0.007499999832361937 * power + 0.20000000298023224;
            double velZ = random.nextGaussian() * 0.007499999832361937 * power + (double) face.getModZ() * d3;
            newItem.setVelocity(new Vector(velX, velY, velZ));
        });

        return true;
    }


}
