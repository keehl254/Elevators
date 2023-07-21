package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.models.ElevatorSearchResult;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ElevatorVersionService;
import com.lkeehl.elevators.services.ObstructionService;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.Map;

public class ElevatorHelper {

    static boolean isElevator(ShulkerBox box) {
        return ElevatorVersionService.getElevatorType(box) != null;
    }

    static ElevatorType getElevatorType(ItemStack item) {
        return ElevatorVersionService.getElevatorType(item);
    }

    static ElevatorType getElevatorType(Block block) {
        return ElevatorVersionService.getElevatorType(block);
    }

    static ElevatorType getElevatorType(ShulkerBox box) {
        return ElevatorVersionService.getElevatorType(box);
    }

    static int getFloorNumberOrCount(ShulkerBox box, ElevatorType elevatorType, boolean stopAtProvidedBox) {
        World world = box.getWorld();
        int worldMinHeight = MCVersionHelper.getWorldMinHeight(world);

        Location startingLocation = box.getLocation();
        startingLocation.setY(worldMinHeight);

        int floor = 0;
        ElevatorSearchResult searchResult;
        do {
            searchResult = findDestinationElevator(null, startingLocation, elevatorType, box.getColor(), (byte) 1, false, false, true);
            if(searchResult == null)
                continue;
            floor++;

            startingLocation = searchResult.getDestination().getLocation();
            if(stopAtProvidedBox && startingLocation.getY() == box.getY())
                break;
        } while(searchResult != null);

        return floor;
    }

    static ElevatorSearchResult findDestinationElevator(Player player, ShulkerBox origin, ElevatorType elevatorType, byte direction) {
        return findDestinationElevator(player, origin.getLocation(), elevatorType, origin.getColor(), direction, false, false, false);
    }

    static ElevatorSearchResult findDestinationElevator(Player player, Location originLocation, ElevatorType elevatorType, DyeColor elevatorColor,  byte direction, boolean ignoreSolidBlockCheck, boolean ignoreDistanceCheck, boolean ignoreObstructionCheck) {

        World world = originLocation.getWorld();
        if(world == null)
            return null;

        int worldMinHeight = MCVersionHelper.getWorldMinHeight(world);
        int maxDistance = elevatorType.getMaxDistanceAllowedBetweenElevators() == -1 || ignoreDistanceCheck ? Short.MAX_VALUE : elevatorType.getMaxDistanceAllowedBetweenElevators();

        int endPointY = Math.min(world.getMaxHeight(), Math.max(worldMinHeight, originLocation.getBlockY() + (maxDistance * direction)));

        int solidBlocks = elevatorType.getMaxSolidBlocksAllowedBetweenElevators() == -1 || ignoreSolidBlockCheck ? Short.MIN_VALUE : 0;
        Location tempLocation = originLocation.clone();
        do {
            tempLocation = tempLocation.add(0,direction,0);
            Block tempBlock = tempLocation.getBlock();

            if (tempBlock.getType().isSolid())
                solidBlocks++;

            BlockState tempBlockState = tempBlock.getState();
            if(!ShulkerBoxHelper.isShulkerBox(tempBlockState))
                continue;

            ShulkerBox tempShulkerBox = (ShulkerBox) tempBlockState;
            ElevatorType tempElevatorType = ElevatorHelper.getElevatorType(tempShulkerBox);
            if(tempElevatorType == null || (elevatorType.checkDestinationElevatorType() && !elevatorType.equals(tempElevatorType)))
                continue;

            if(--solidBlocks >= elevatorType.getMaxSolidBlocksAllowedBetweenElevators())
                return null;

            if(tempShulkerBox.getColor() != elevatorColor && !elevatorType.canTeleportToOtherColor())
                continue;

            if(elevatorType.canTeleportToObstructedBlock() || ignoreObstructionCheck)
                return new ElevatorSearchResult(originLocation,tempShulkerBox, 0.0D);

            double addition = player != null ? ObstructionService.getHitBoxAddition(tempBlock.getRelative(BlockFace.UP), player) : 0.0;
            if (addition >= 0)
                return new ElevatorSearchResult(originLocation, tempShulkerBox, Math.abs(addition));
        } while(tempLocation.getBlockY() != endPointY);

        return null;
    }

}
