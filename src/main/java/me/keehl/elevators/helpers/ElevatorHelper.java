package me.keehl.elevators.helpers;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.events.ElevatorMenuOpenEvent;
import me.keehl.elevators.events.ElevatorUseEvent;
import me.keehl.elevators.menus.interact.InteractMenu;
import me.keehl.elevators.models.*;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
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

public class ElevatorHelper {

    public static boolean isElevator(InventoryHolder inventoryHolder) {
        if (!(inventoryHolder instanceof BlockState))
            return false;
        return ElevatorHelper.isElevator((BlockState) inventoryHolder);
    }

    public static boolean isElevator(BlockState blockState) {
        if (!ShulkerBoxHelper.isShulkerBox(blockState))
            return false;

        return Elevators.getVersionService().getElevatorType((ShulkerBox) blockState, false) != null;
    }

    public static boolean isElevator(ShulkerBox box) {
        return Elevators.getVersionService().getElevatorType(box, false) != null;
    }

    public static boolean isElevator(Block block) {
        return Elevators.getVersionService().getElevatorType(block) != null;
    }

    public static boolean isElevator(ItemStack itemStack) {
        return Elevators.getVersionService().getElevatorType(itemStack) != null;
    }

    public static boolean isElevator(Item item) {
        return Elevators.getVersionService().getElevatorType(item.getItemStack()) != null;
    }

    public static IElevatorType getElevatorType(ItemStack item) {
        return Elevators.getVersionService().getElevatorType(item);
    }

    public static IElevatorType getElevatorType(Block block) {
        return Elevators.getVersionService().getElevatorType(block);
    }

    public static IElevatorType getElevatorType(ShulkerBox box) {
        return Elevators.getVersionService().getElevatorType(box, true);
    }

    public static IElevatorType getElevatorType(ShulkerBox box, boolean updateBlock) {
        return Elevators.getVersionService().getElevatorType(box, updateBlock);
    }

    public static int getFloorNumberOrCount(IElevator elevator, boolean stopAtProvidedBox) {
        Location startingLocation = elevator.getLocation();
        IElevatorEventData searchResult;

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

    public static IElevatorEventData findDestinationElevator(Player player, IElevator elevator, byte direction) {
        return findDestinationElevator(player, elevator.getLocation(), elevator, direction, false, false, false);
    }

    public static IElevatorEventData findDestinationElevator(Player player, Location origin, IElevator elevator, byte direction, boolean ignoreSolidBlockCheck, boolean ignoreDistanceCheck, boolean ignoreObstructionCheck) {
        direction = (byte) (direction > 0 ? 1 : -1);
        if (direction == -1 && origin.getBlockY() == VersionHelper.getWorldMinHeight(origin.getWorld()))
            return null;

        World world = origin.getWorld();

        int worldMinHeight = VersionHelper.getWorldMinHeight(world);
        int maxDistance = Elevators.getSettingService().getElevatorSettingValue(elevator, InternalElevatorSettingType.MAX_DISTANCE);
        if (maxDistance == -1 || ignoreDistanceCheck)
            maxDistance = Short.MAX_VALUE;

        int endPointY = Math.min(Math.max(origin.getBlockY() + (maxDistance * direction), worldMinHeight), world.getMaxHeight());

        boolean stopsObstruction = Elevators.getSettingService().getElevatorSettingValue(elevator, InternalElevatorSettingType.STOP_OBSTRUCTION);
        boolean checkColor = Elevators.getSettingService().getElevatorSettingValue(elevator, InternalElevatorSettingType.CHECK_COLOR);
        boolean checksClass = Elevators.getSettingService().getElevatorSettingValue(elevator, InternalElevatorSettingType.CLASS_CHECK);
        int maxSolidBlocks = Elevators.getSettingService().getElevatorSettingValue(elevator, InternalElevatorSettingType.MAX_SOLID_BLOCKS);

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

            IElevatorType tempElevatorType = ElevatorHelper.getElevatorType(tempShulkerBox, false);
            IElevator tempElevator = new Elevator(tempShulkerBox, tempElevatorType);
            if (tempElevatorType == null || (checksClass && !elevator.getElevatorType().equals(tempElevatorType)))
                continue;

            if (--solidBlocks >= maxSolidBlocks)
                return null;

            if (tempShulkerBox.getColor() != elevator.getDyeColor() && checkColor)
                continue;

            if (!stopsObstruction || ignoreObstructionCheck)
                return new ElevatorEventData(player, elevator, tempElevator, direction, 0.0D);

            double addition = player != null ? Elevators.getObstructionService().getHitBoxAddition(tempBlock.getRelative(BlockFace.UP), player) : 0.0;
            if (addition >= 0)
                return new ElevatorEventData(player, elevator, tempElevator, direction, Math.abs(addition));
        } while (tempLocation.getBlockY() != endPointY);

        return null;
    }

    public static void setElevatorDisabled(ShulkerBox shulkerBox) {
        shulkerBox.setMetadata("elevator-disabled", new FixedMetadataValue(Elevators.getInstance(), true));
    }

    public static void setElevatorEnabled(ShulkerBox shulkerBox) {
        shulkerBox.removeMetadata("elevator-disabled", Elevators.getInstance());
    }

    public static void resetElevatorEditState(IElevator elevator) {
        ElevatorHelper.setElevatorEnabled(elevator.getShulkerBox());
        ShulkerBoxHelper.playClose(elevator.getShulkerBox());

        elevator.getShulkerBox().removeMetadata("open-player", Elevators.getInstance());
    }

    public static void onElevatorInteract(Player player, PlayerInteractEvent event, IElevator elevator) {
        if (isElevatorDisabled(elevator.getShulkerBox()))
            return; // TODO: Message that elevator is temporarily unable to be interacted with.

        Elevators.getHologramService().updateElevatorHologram(elevator);

        if(!elevator.getElevatorType(true).shouldAllowIndividualEdit())
            return;

        if (!Elevators.getHooksService().canEditElevator(player, elevator, true)) {

            List<MetadataValue> values = elevator.getShulkerBox().getMetadata("open-player");
            List<Player> players = values.stream().map(MetadataValue::asString).map(UUID::fromString).map(Bukkit::getPlayer).toList();

            boolean reset = players.isEmpty() || players.getFirst().getUniqueId().equals(player.getUniqueId());
            if(reset) {
                ElevatorHelper.resetElevatorEditState(elevator);
            }
            return;
        }

        /* I would prefer to check if it's canceled here and then call the Gui helper... But the Gui Helper needs access to
         dependencies only available in the Hooks module.

         It may be worth considering restructuring the modules. Why go through the pain of creating "Core" in java 8,
         creating "Hooks" in 21, shading in "Core", and then downgrading Hooks to Java 8. I could just make it all in 21
         and then downgrade it. Just added complexity for anyone else trying to build Elevators.
         */
        ElevatorMenuOpenEvent menuOpenEvent = new ElevatorMenuOpenEvent(player, event, elevator);
        Bukkit.getPluginManager().callEvent(menuOpenEvent);
        if(menuOpenEvent.isCancelled())
            return;

        InteractMenu.openInteractMenu(menuOpenEvent.getPlayer(), menuOpenEvent.getElevator());
    }

    public static void onElevatorPlace(IElevator elevator) {

    }

    /*
    I hate using TeleportCause.UNKNOWN, but it's the only way to stop CMI and Essentials from registering the teleport
    as a /back point
    */
    public static void onElevatorUse(Player player, IElevatorEventData elevatorEventData) {
        List<IElevatorAction> actions;
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
            MetadataValue value = player.getMetadata(key).getFirst();
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
