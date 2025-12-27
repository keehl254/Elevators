package me.keehl.elevators.helpers;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.events.ElevatorUseEvent;
import me.keehl.elevators.models.*;
import me.keehl.elevators.services.*;
import me.keehl.elevators.util.InternalElevatorSettingType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ElevatorHelper {

    public static boolean isElevator(InventoryHolder inventoryHolder) {
        if (!(inventoryHolder instanceof BlockState))
            return false;
        return ElevatorHelper.isElevator((BlockState) inventoryHolder);
    }

    public static boolean isElevator(BlockState blockState) {
        if (!ShulkerBoxHelper.isShulkerBox(blockState))
            return false;

        return ElevatorVersionService.getElevatorType((ShulkerBox) blockState, false) != null;
    }

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
        while (true) {
            searchResult = findDestinationElevator(null, startingLocation, elevator, (byte) -1, false, false, true);
            if (searchResult == null) break;

            startingLocation = searchResult.getDestination().getLocation();
        }

        if ((elevator.getLocation().getBlockY() == startingLocation.getBlockY()) && stopAtProvidedBox)
            return 1;

        // Start upwards iteration
        int floor = 1;
        while (true) {
            searchResult = findDestinationElevator(null, startingLocation, elevator, (byte) 1, false, false, true);
            if (searchResult == null) break;
            floor++;

            startingLocation = searchResult.getDestination().getLocation();
            if (stopAtProvidedBox && startingLocation.getY() == elevator.getLocation().getBlockY())
                break;
        }

        return floor;
    }

    public static ElevatorEventData findDestinationElevator(Player player, Elevator elevator, byte direction) {
        return findDestinationElevator(player, elevator.getLocation(), elevator, direction, false, false, false);
    }

    public static ElevatorEventData findDestinationElevator(Player player, Location origin, Elevator elevator, byte direction, boolean ignoreSolidBlockCheck, boolean ignoreDistanceCheck, boolean ignoreObstructionCheck) {
        direction = (byte) (direction > 0 ? 1 : -1);
        if (direction == -1 && origin.getBlockY() == VersionHelper.getWorldMinHeight(origin.getWorld()))
            return null;

        World world = origin.getWorld();

        int worldMinHeight = VersionHelper.getWorldMinHeight(world);
        int maxDistance = ElevatorSettingService.getElevatorSettingValue(elevator, InternalElevatorSettingType.MAX_DISTANCE);
        if (maxDistance == -1 || ignoreDistanceCheck)
            maxDistance = Short.MAX_VALUE;

        int endPointY = Math.min(Math.max(origin.getBlockY() + (maxDistance * direction), worldMinHeight), world.getMaxHeight());

        boolean stopsObstruction = ElevatorSettingService.getElevatorSettingValue(elevator, InternalElevatorSettingType.STOP_OBSTRUCTION);
        boolean checkColor = ElevatorSettingService.getElevatorSettingValue(elevator, InternalElevatorSettingType.CHECK_COLOR);
        boolean checksClass = ElevatorSettingService.getElevatorSettingValue(elevator, InternalElevatorSettingType.CLASS_CHECK);
        int maxSolidBlocks = ElevatorSettingService.getElevatorSettingValue(elevator, InternalElevatorSettingType.MAX_SOLID_BLOCKS);

        int solidBlocks = maxSolidBlocks == -1 || ignoreSolidBlockCheck ? Short.MIN_VALUE : 0;
        Location tempLocation = origin.clone();
        do {
            tempLocation = tempLocation.add(0, direction, 0);
            Block tempBlock = tempLocation.getBlock();

            if (tempBlock.getType().isSolid())
                solidBlocks++;

            ShulkerBox tempShulkerBox = ShulkerBoxHelper.getShulkerBox(tempBlock);
            if (tempShulkerBox == null)
                continue;

            ElevatorType tempElevatorType = ElevatorHelper.getElevatorType(tempShulkerBox, false);
            Elevator tempElevator = new Elevator(tempShulkerBox, tempElevatorType);
            if (tempElevatorType == null || (checksClass && !elevator.getElevatorType().equals(tempElevatorType)))
                continue;

            if (--solidBlocks >= maxSolidBlocks)
                return null;

            if (tempShulkerBox.getColor() != elevator.getDyeColor() && checkColor)
                continue;

            if (!stopsObstruction || ignoreObstructionCheck)
                return new ElevatorEventData(elevator, tempElevator, direction, 0.0D);

            double addition = player != null ? ElevatorObstructionService.getHitBoxAddition(tempBlock.getRelative(BlockFace.UP), player) : 0.0;
            if (addition >= 0)
                return new ElevatorEventData(elevator, tempElevator, direction, Math.abs(addition));
        } while (tempLocation.getBlockY() != endPointY);

        return null;
    }

    public static void setElevatorDisabled(ShulkerBox shulkerBox) {
        shulkerBox.setMetadata("elevator-disabled", new FixedMetadataValue(Elevators.getInstance(), true));
    }

    public static void setElevatorEnabled(ShulkerBox shulkerBox) {
        shulkerBox.removeMetadata("elevator-disabled", Elevators.getInstance());
    }

    public static void resetElevatorEditState(Elevator elevator) {
        ElevatorHelper.setElevatorEnabled(elevator.getShulkerBox());
        ShulkerBoxHelper.playClose(elevator.getShulkerBox());

        elevator.getShulkerBox().removeMetadata("open-player", Elevators.getInstance());
    }

    public static void onElevatorInteract(Player player, PlayerInteractEvent event, Elevator elevator) {
        if (isElevatorDisabled(elevator.getShulkerBox()))
            return; // TODO: Message that elevator is temporarily unable to be interacted with.

        ElevatorHologramService.updateElevatorHologram(elevator);

        if(!elevator.getElevatorType(true).shouldAllowIndividualEdit())
            return;

        if (!ElevatorHookService.canEditElevator(player, elevator, true)) {

            List<MetadataValue> values = elevator.getShulkerBox().getMetadata("open-player");
            List<Player> players = values.stream().map(MetadataValue::asString).map(UUID::fromString).map(Bukkit::getPlayer).collect(Collectors.toList());

            boolean reset = players.isEmpty() || players.get(0).getUniqueId().equals(player.getUniqueId());
            if(reset) {
                ElevatorHelper.resetElevatorEditState(elevator);
            } else
                return;
        }

        ElevatorGUIHelper.openInteractMenu(event.getPlayer(), elevator);
    }

    public static void onElevatorPlace(Elevator elevator) {

    }

    /*
    I hate using TeleportCause.UNKNOWN, but it's the only way to stop CMI and Essentials from registering the teleport
    as a /back point
    */
    public static void onElevatorUse(Player player, ElevatorEventData elevatorEventData) {
        List<ElevatorAction> actions;
        if (elevatorEventData.getDirection() == 1)
            actions = elevatorEventData.getOrigin().getElevatorType().getActionsUp();
        else
            actions = elevatorEventData.getOrigin().getElevatorType().getActionsDown();

        if(actions.stream().anyMatch(action -> !action.meetsConditions(elevatorEventData, player)))
            return;

        ElevatorUseEvent useEvent = new ElevatorUseEvent(player, elevatorEventData);
        Bukkit.getPluginManager().callEvent(useEvent);
        if (useEvent.isCancelled()) return;

        actions.forEach(action -> action.execute(elevatorEventData, player));

        Location teleportLocation = player.getLocation();
        teleportLocation.setY(elevatorEventData.getDestination().getLocation().getBlockY() + elevatorEventData.getStandOnAddition() + 1.0);
        Elevators.getFoliaLib().getScheduler().teleportAsync(player, teleportLocation, PlayerTeleportEvent.TeleportCause.UNKNOWN);
    }

    public static boolean hasOrAddPlayerCoolDown(Player player, String key) {
        key = "elevator-cooldown-" + key;
        if (player.hasMetadata(key)) {
            MetadataValue value = player.getMetadata(key).get(0);
            long lastTime = value.asLong();
            if (System.currentTimeMillis() - lastTime < 1000)
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
