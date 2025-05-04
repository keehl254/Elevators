package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.*;
import com.lkeehl.elevators.models.settings.*;
import com.lkeehl.elevators.services.*;
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

    public static int getFloorNumberOrCount(Elevator elevator, boolean stopAtProvidedBox) {
        Location startingLocation = elevator.getLocation();
        ElevatorEventData searchResult;

        // Iterate downwards to find the lowest floor
        while(true) {
            searchResult = findDestinationElevator(null, startingLocation, elevator, (byte) -1, false, false, true);
            if(searchResult == null) break;

            startingLocation = searchResult.getDestination().getLocation();
        }

        if((elevator.getLocation().getBlockY() == startingLocation.getBlockY()) && stopAtProvidedBox)
            return 1;

        // Start upwards iteration
        int floor = 1;
        while(true) {
            searchResult = findDestinationElevator(null, startingLocation, elevator, (byte) 1, false, false, true);
            if(searchResult == null) break;
            floor++;

            startingLocation = searchResult.getDestination().getLocation();
            if(stopAtProvidedBox && startingLocation.getY() == elevator.getLocation().getBlockY())
                break;
        }

        return floor;
    }

    public static ElevatorEventData findDestinationElevator(Player player, Elevator elevator, byte direction) {
        return findDestinationElevator(player, elevator.getLocation(), elevator, direction, false, false, false);
    }

    public static ElevatorEventData findDestinationElevator(Player player, Location origin, Elevator elevator, byte direction, boolean ignoreSolidBlockCheck, boolean ignoreDistanceCheck, boolean ignoreObstructionCheck) {
        direction = (byte) (direction > 0 ? 1 : -1);
        if(direction == -1 && origin.getBlockY() == MCVersionHelper.getWorldMinHeight(origin.getWorld()))
            return null;

        World world = origin.getWorld();

        int worldMinHeight = MCVersionHelper.getWorldMinHeight(world);
        int maxDistance = ElevatorSettingService.getSettingValue(elevator, MaxDistanceSetting.class);
        if(maxDistance == -1 || ignoreDistanceCheck)
            maxDistance = Short.MAX_VALUE;

        int endPointY = Math.clamp(origin.getBlockY() + (maxDistance * direction), worldMinHeight, world.getMaxHeight());

        boolean stopsObstruction = ElevatorSettingService.getSettingValue(elevator, StopObstructionSetting.class);
        boolean checkColor = ElevatorSettingService.getSettingValue(elevator, CheckColorSetting.class);
        boolean checksClass = ElevatorSettingService.getSettingValue(elevator, ClassCheckSetting.class);
        int maxSolidBlocks = ElevatorSettingService.getSettingValue(elevator, MaxSolidBlocksSetting.class);

        int solidBlocks = maxSolidBlocks == -1 || ignoreSolidBlockCheck ? Short.MIN_VALUE : 0;
        Location tempLocation = origin.clone();
        do {
            tempLocation = tempLocation.add(0,direction,0);
            Block tempBlock = tempLocation.getBlock();

            if (tempBlock.getType().isSolid())
                solidBlocks++;

            BlockState tempBlockState = tempBlock.getState();
            if(!ShulkerBoxHelper.isShulkerBox(tempBlockState))
                continue;

            ShulkerBox tempShulkerBox = (ShulkerBox) tempBlockState;
            ElevatorType tempElevatorType = ElevatorHelper.getElevatorType(tempShulkerBox, false);
            Elevator tempElevator = new Elevator(tempShulkerBox, tempElevatorType);
            if(tempElevatorType == null || (checksClass && !elevator.getElevatorType().equals(tempElevatorType)))
                continue;

            if(--solidBlocks >= maxSolidBlocks)
                return null;

            if(tempShulkerBox.getColor() != elevator.getDyeColor() && checkColor)
                continue;

            if(!stopsObstruction || ignoreObstructionCheck)
                return new ElevatorEventData(elevator, tempElevator, direction, 0.0D);

            double addition = player != null ? ElevatorObstructionService.getHitBoxAddition(tempBlock.getRelative(BlockFace.UP), player) : 0.0;
            if (addition >= 0)
                return new ElevatorEventData(elevator, tempElevator, direction, Math.abs(addition));
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
        if(isElevatorDisabled(elevator.getShulkerBox()))
            return; // TODO: Message that elevator is temporarily unable to be interacted with.

        ElevatorHologramService.updateElevatorHologram(elevator);

        if(!ElevatorHookService.canEditElevator(player, elevator, true))
            return;

        ElevatorGUIHelper.openInteractMenu(event.getPlayer(), elevator);
    }

    public static void onElevatorPlace(Elevator belevator) {

    }

    public static void onElevatorUse(Player player, ElevatorEventData elevatorEventData) {
        List<ElevatorAction> actions;
        if(elevatorEventData.getDirection() == 1)
            actions =  elevatorEventData.getOrigin().getElevatorType().getActionsUp();
        else
            actions =  elevatorEventData.getOrigin().getElevatorType().getActionsDown();

        actions.forEach(action -> action.execute(elevatorEventData,  player));

        Location teleportLocation = player.getLocation();
        teleportLocation.setY(elevatorEventData.getDestination().getLocation().getBlockY() + elevatorEventData.getStandOnAddition() + 1.0);
        player.teleport(teleportLocation);
    }

    public static boolean hasOrAddPlayerCoolDown(Player player, String key) {
        key = "elevator-cooldown-"+key;
        if(player.hasMetadata(key)) {
            MetadataValue value = player.getMetadata(key).getFirst();
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
