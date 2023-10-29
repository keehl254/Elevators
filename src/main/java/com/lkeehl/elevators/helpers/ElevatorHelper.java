package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.*;
import com.lkeehl.elevators.services.ElevatorVersionService;
import com.lkeehl.elevators.services.ObstructionService;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class ElevatorHelper {

    public static boolean isElevator(ShulkerBox box) {
        return ElevatorVersionService.getElevatorType(box, false) != null;
    }

    public static boolean isElevator(Block block) {
        return ElevatorVersionService.getElevatorType(block) != null;
    }

    public static boolean isElevator(ItemStack itemStack) {
        return ElevatorVersionService.getElevatorType(itemStack) != null;
    }

    public static boolean isElevator(Item item) {
        return ElevatorVersionService.getElevatorType(item.getItemStack()) != null;
    }

    public static ElevatorType getElevatorType(ItemStack item) {
        return ElevatorVersionService.getElevatorType(item);
    }

    public static ElevatorType getElevatorType(Block block) {
        return ElevatorVersionService.getElevatorType(block);
    }

    public static ElevatorType getElevatorType(ShulkerBox box) {
        return ElevatorVersionService.getElevatorType(box, true);
    }

    public static ElevatorType getElevatorType(ShulkerBox box, boolean updateBlock) {
        return ElevatorVersionService.getElevatorType(box, updateBlock);
    }

    public static int getFloorNumberOrCount(ShulkerBox box, ElevatorType elevatorType, boolean stopAtProvidedBox) {
        World world = box.getWorld();
        int worldMinHeight = MCVersionHelper.getWorldMinHeight(world);

        Location startingLocation = box.getLocation();
        startingLocation.setY(worldMinHeight);

        int floor = 0;
        ElevatorEventData searchResult = new ElevatorEventData(box, elevatorType, null, (byte) 1, 0.0F);
        do {
            searchResult = findDestinationElevator(null, searchResult.getOrigin(), elevatorType, box.getColor(), (byte) 1, false, false, true);
            if(searchResult == null)
                continue;
            floor++;

            startingLocation = searchResult.getDestination().getLocation();
            if(stopAtProvidedBox && startingLocation.getY() == box.getY())
                break;
        } while(searchResult != null);

        return floor;
    }

    public static ElevatorEventData findDestinationElevator(Player player, ShulkerBox origin, ElevatorType elevatorType, byte direction) {
        return findDestinationElevator(player, origin, elevatorType, origin.getColor(), direction, false, false, false);
    }

    public static ElevatorEventData findDestinationElevator(Player player, ShulkerBox origin, ElevatorType elevatorType, DyeColor elevatorColor, byte direction, boolean ignoreSolidBlockCheck, boolean ignoreDistanceCheck, boolean ignoreObstructionCheck) {

        World world = origin.getWorld();

        int worldMinHeight = MCVersionHelper.getWorldMinHeight(world);
        int maxDistance = elevatorType.getMaxDistanceAllowedBetweenElevators() == -1 || ignoreDistanceCheck ? Short.MAX_VALUE : elevatorType.getMaxDistanceAllowedBetweenElevators();

        int endPointY = Math.min(world.getMaxHeight(), Math.max(worldMinHeight, origin.getY() + (maxDistance * direction)));

        int solidBlocks = elevatorType.getMaxSolidBlocksAllowedBetweenElevators() == -1 || ignoreSolidBlockCheck ? Short.MIN_VALUE : 0;
        Location tempLocation = origin.getLocation().clone();
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
                return new ElevatorEventData(origin, elevatorType, tempShulkerBox, direction, 0.0D);

            double addition = player != null ? ObstructionService.getHitBoxAddition(tempBlock.getRelative(BlockFace.UP), player) : 0.0;
            if (addition >= 0)
                return new ElevatorEventData(origin, elevatorType, tempShulkerBox, direction, Math.abs(addition));
        } while(tempLocation.getBlockY() != endPointY);

        return null;
    }

    public static void setElevatorDisabled(ShulkerBox shulkerBox) {
        shulkerBox.setMetadata("elevator-disabled", new FixedMetadataValue(Elevators.getInstance(), true));
    }

    public static void setElevatorEnabled(ShulkerBox shulkerBox) {
        shulkerBox.removeMetadata("elevator-disabled", Elevators.getInstance());
    }

    public static void onElevatorInteract(Player player, PlayerInteractEvent event, Elevator elevator) {

    }

    public static void onElevatorPlace(Elevator elevator) {

    }

    public static void onElevatorUse(Player player, ElevatorEventData elevatorEventData) {
        ElevatorEffect effect;
        List<ElevatorAction> actions;
        if(elevatorEventData.getDirection() == 1) {
            effect = elevatorEventData.getElevatorType().getElevatorUpEffect();
            actions =  elevatorEventData.getElevatorType().getActionsUp();
        }else {
            effect = elevatorEventData.getElevatorType().getElevatorDownEffect();
            actions =  elevatorEventData.getElevatorType().getActionsDown();
        }

        actions.forEach(action -> action.execute(elevatorEventData,  player));
        effect.playEffect(elevatorEventData);

        Location teleportLocation = player.getLocation();
        teleportLocation.setY(elevatorEventData.getDestination().getY() + elevatorEventData.getStandOnAddition() + 1.0);
        player.teleport(teleportLocation);
    }

    public static boolean hasOrAddPlayerCoolDown(Player player, String key) {
        key = "elevator-cooldown-"+key;
        if(player.hasMetadata(key)) {
            MetadataValue value = player.getMetadata(key).get(0);
            long lastTime = value.asLong();
            if(System.currentTimeMillis() - lastTime < 1000)
                return true;
            player.removeMetadata(key, Elevators.getInstance());
        }
        player.setMetadata(key, new FixedMetadataValue(Elevators.getInstance(), System.currentTimeMillis()));
        return false;
    }

    public static boolean isElevatorDisabled(ShulkerBox shulkerBox) {
        return shulkerBox.hasMetadata("elevator-disabled");

    }



}
