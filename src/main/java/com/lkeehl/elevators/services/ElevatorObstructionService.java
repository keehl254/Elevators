package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.MCVersionHelper;
import com.lkeehl.elevators.helpers.ResourceHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ElevatorObstructionService {

    private final static List<Material> nonObstructiveMaterials = new ArrayList<>();

    private static boolean initialized = false;

    public static void init() {
        if (ElevatorObstructionService.initialized)
            return;

        ElevatorConfigService.addConfigCallback(i -> ElevatorObstructionService.loadNonObstructiveMaterials());

        initialized = true;
    }

    private static void loadNonObstructiveMaterials() {
        nonObstructiveMaterials.clear();

        List<String> lines = null;
        try {
            if (!MCVersionHelper.doesVersionSupportBlockBoundingBoxes()) {
                File nonObstructiveMaterialsFile = new File(Elevators.getConfigDirectory(), "nonobstructiveMaterials.txt");

                ResourceHelper.exportResource(Elevators.getInstance(), "nonobstructiveMaterials.txt", nonObstructiveMaterialsFile, false);
                lines = Files.readAllLines(nonObstructiveMaterialsFile.toPath());
            } else {
                Elevators elevators = Elevators.getInstance();
                if (elevators == null)
                    return;
                ClassLoader classLoader = elevators.getClass().getClassLoader();
                if (classLoader == null)
                    return;
                try (InputStream is = classLoader.getResourceAsStream("nonobstructiveMaterials.txt")) {
                    if (is == null)
                        return;
                    lines = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
                } catch (Exception ignore) {
                }
            }
        } catch (Exception ignore) {
        }

        if (lines == null) {
            Elevators.getElevatorsLogger().warning("Failed to load non-obstructive materials list!");
            return;
        }

        for (String line : lines) {
            Material type = Material.matchMaterial(line);
            if (type != null)
                nonObstructiveMaterials.add(type);
        }

    }

    public static boolean isObstructive(Material type) {
        return !nonObstructiveMaterials.contains(type);
    }

    public static boolean isBlockObstructed(Block block, int height) {
        Location testObstruction = block.getLocation();
        for (int i = 0; i < height; i++) {
            if (ElevatorObstructionService.isObstructive(testObstruction.add(0.0D, 1.0D, 0.0D).getBlock().getType()))
                return true;
        }

        return false;
    }

    public static double getHitBoxAddition(Block block, Player player) {

        if (!MCVersionHelper.doesVersionSupportBlockBoundingBoxes())
            return ElevatorObstructionService.isBlockObstructed(block, 2) ? -1 : 0;

        Location location = player.getLocation();
        double newX = location.getX() < 0 ? 1.0 + location.getX() % 1 : location.getX() % 1;
        double newZ = location.getZ() < 0 ? 1.0 + location.getZ() % 1 : location.getZ() % 1;

        BoundingBox box = new BoundingBox(newX - 0.3, 0.0, newZ - 0.3, newX + 0.3, 1.8, newZ + 0.3);

        double bottomOverlapY = 0;
        double topOverlapY = 0;

        if (block.getType() != Material.AIR && !block.isPassable()) {
            for (BoundingBox blockBB : block.getCollisionShape().getBoundingBoxes()) {
                if (blockBB.overlaps(box))
                    bottomOverlapY = Math.max(bottomOverlapY, blockBB.intersection(box).getMaxY());
            }
        }
        Block tempBlock = block;
        BoundingBox cloneBox = new BoundingBox(box.getMinX(), box.getMinY() - bottomOverlapY, box.getMinZ(), box.getMaxX(), box.getMaxY() - bottomOverlapY, box.getMaxZ());
        for (int i = 1; i <= 2; i++) {
            topOverlapY = i + 1;
            tempBlock = tempBlock.getRelative(BlockFace.UP);

            if (tempBlock.isPassable()) // TODO: OR material is in nonobstructivematerials list. OR material isn't in ElevatorType isObstructing method.
                continue;

            for (BoundingBox blockBB : tempBlock.getCollisionShape().getBoundingBoxes()) {
                if (blockBB.overlaps(cloneBox))
                    topOverlapY = Math.min(topOverlapY, i + blockBB.intersection(cloneBox).getMinY());
            }

            if (topOverlapY != i + 1)
                break;
        }

        double height = topOverlapY - bottomOverlapY;
        if (height < 1.8)
            return -1;
        double addition = height >= box.getHeight() ? bottomOverlapY : -bottomOverlapY;
        return addition >= 1.0 ? -1 : addition;
    }

}
