package me.keehl.elevators.services;

import me.keehl.elevators.helpers.VersionHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class ElevatorObstructionService {

    private static boolean initialized = false;

    public static void init() {
        if (ElevatorObstructionService.initialized)
            return;

        initialized = true;
    }

    public static double getHitBoxAddition(Block block, Player player) {

        BoundingBox originalBox = player.getBoundingBox();

        double newMinX = originalBox.getMinX() < 0 ? 1.0 + originalBox.getMinX() % 1 : originalBox.getMinX() % 1;
        double newMinZ = originalBox.getMinZ() < 0 ? 1.0 + originalBox.getMinZ() % 1 : originalBox.getMinZ() % 1;
        double newMinY = originalBox.getMinY() < 0 ? 1.0 + originalBox.getMinY() % 1 : originalBox.getMinY() % 1;

        BoundingBox box = new BoundingBox(newMinX, newMinY, newMinZ, newMinX + originalBox.getWidthX(), newMinY + originalBox.getHeight(), newMinZ + originalBox.getWidthZ());

        double bottomOverlapY = 0;
        double topOverlapY = 0;

        if (block.getType() != Material.AIR && !block.isPassable()) {
            for (BoundingBox blockBB : VersionHelper.getBoundingBoxes(block)) {

                if (blockBB.overlaps(box)) {
                    bottomOverlapY = Math.max(bottomOverlapY, blockBB.intersection(box).getMaxY());
                }
            }
        }
        Block tempBlock = block;
        BoundingBox cloneBox = new BoundingBox(box.getMinX(), box.getMinY() - bottomOverlapY, box.getMinZ(), box.getMaxX(), box.getMaxY() - bottomOverlapY, box.getMaxZ());
        for (int i = 1; i <= 2; i++) {
            topOverlapY = i + 1;
            tempBlock = tempBlock.getRelative(BlockFace.UP);

            if (tempBlock.isPassable())
                continue;

            for (BoundingBox blockBB : VersionHelper.getBoundingBoxes(tempBlock)) {
                if (blockBB.overlaps(cloneBox))
                    topOverlapY = Math.min(topOverlapY, i + blockBB.intersection(cloneBox).getMinY());
            }

            if (topOverlapY != i + 1)
                break;
        }

        double height = topOverlapY - bottomOverlapY;
        if (height < box.getHeight())
            return -1;
        double addition = height >= box.getHeight() ? bottomOverlapY : -bottomOverlapY;
        return addition >= 1.0 ? -1 : addition;
    }

}
