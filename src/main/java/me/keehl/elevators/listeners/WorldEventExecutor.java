package me.keehl.elevators.listeners;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.helpers.*;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorEventData;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Item;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

public class WorldEventExecutor {

    public static void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (ItemStackHelper.isNotShulkerBox(block.getType()))
                continue;
            if (ElevatorHelper.isElevator(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    public static void onExplode(EntityExplodeEvent event) {
        for (int i = 0; i < new ArrayList<>(event.blockList()).size(); i++) {
            Block block = event.blockList().get(i);
            ShulkerBox shulkerBox = ShulkerBoxHelper.getShulkerBox(block);
            if (shulkerBox == null)
                continue;

            IElevatorType elevatorType = ElevatorHelper.getElevatorType(shulkerBox);
            if (elevatorType == null)
                continue;

            IElevator elevator = new Elevator(shulkerBox, elevatorType);

            event.blockList().remove(block);

            if (Elevators.getSettingService().getElevatorSettingValue(elevator, InternalElevatorSettingType.CAN_EXPLODE)) {
                final ItemStack newItem = ItemStackHelper.createItemStackFromElevator(elevator);
                final Location location = block.getLocation();
                Elevators.getFoliaLib().getScheduler().runAtLocation(location, task -> {
                    location.getBlock().setType(Material.AIR);
                    location.getWorld().dropItemNaturally(location, newItem);
                });
            }
        }
    }

    public static void onDispenserPlace(BlockDispenseEvent event) {
        if (ItemStackHelper.isNotShulkerBox(event.getItem().getType())) return;

        IElevatorType elevatorType = ElevatorHelper.getElevatorType(event.getItem());
        if (elevatorType == null) return;
        if (!event.getBlock().getType().equals(Material.DISPENSER)) return;

        if (!Elevators.getConfigService().getRootConfig().shouldAllowElevatorDispense()) {
            event.setCancelled(true);
            ShulkerBoxHelper.fakeDispense(event.getBlock(), event.getItem());
            return;
        }

        Dispenser dispenser = (Dispenser) event.getBlock().getBlockData();
        Block relative = event.getBlock().getRelative(dispenser.getFacing());
        Elevators.getFoliaLib().getScheduler().runAtLocation(relative.getLocation(), task -> {
            ShulkerBox box = ShulkerBoxHelper.getShulkerBox(relative);
            if (box == null)
                return;

            Elevators.getDataContainerService().updateTypeKeyOnElevator(box, elevatorType);
            Elevators.getDataContainerService().dumpDataFromItemIntoShulkerBox(box, event.getItem());
            ElevatorHelper.onElevatorPlace(new Elevator(box, elevatorType));
            if (Elevators.getConfigService().getRootConfig().shouldForceFacingUpwards())
                ShulkerBoxHelper.setFacingUp(box);
        });
    }

    public static void onBlockBreak(BlockDropItemEvent event) {
        if (!(event.getBlockState() instanceof ShulkerBox box)) return;

        IElevatorType elevatorType = ElevatorHelper.getElevatorType(box, false);
        if (elevatorType == null) return;

        IElevator elevator = new Elevator(box, elevatorType);
        Elevators.getHologramService().deleteHologram(elevator);
        ItemStack newElevatorItem = ItemStackHelper.createItemStackFromElevator(elevator);

        Optional<Item> defaultItem = event.getItems().stream().filter(i -> !ItemStackHelper.isNotShulkerBox(i.getItemStack().getType())).findAny();
        if (defaultItem.isEmpty()) return;

        defaultItem.get().setItemStack(newElevatorItem);
    }

    public static void onBlockPlace(BlockPlaceEvent event) {
        //TODO: Check if elevator is disabled based on location for speed.

        ItemStack item = event.getItemInHand();
        Material type = item.getType();
        if (ItemStackHelper.isNotShulkerBox(type)) return;

        IElevatorType elevatorType = ElevatorHelper.getElevatorType(item);
        if (elevatorType == null) return;

        if (Elevators.getConfigService().isWorldDisabled(event.getBlock().getWorld())) {
            event.setCancelled(true);
            Elevators.getLocale().getWorldDisabledMessage().sendFormatted(event.getPlayer(), new ElevatorEventData(event.getPlayer(), elevatorType));
            return;
        }
        int count = item.getAmount();
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            item.setAmount(count - 1);


        ShulkerBox box = ShulkerBoxHelper.getShulkerBox(event.getBlockPlaced());
        if (box == null)
            return;
        box = Elevators.getDataContainerService().updateTypeKeyOnElevator(box, elevatorType);
        IElevator elevator = new Elevator(box, elevatorType);
        ElevatorHelper.onElevatorPlace(elevator);

        if (Elevators.getConfigService().getRootConfig().shouldForceFacingUpwards())
            ShulkerBoxHelper.setFacingUp(box);

        Elevators.getHologramService().updateElevatorHologram(elevator);
    }


    public static void onChunkLoad(ChunkLoadEvent event) {
        if (!Elevators.getHologramService().canUseHolograms())
            return;

        Elevators.getHologramService().updateHologramsInChunk(event.getChunk());
    }

    public static void onChunkUnload(ChunkUnloadEvent event) {
        if (!Elevators.getHologramService().canUseHolograms())
            return;

        Elevators.getHologramService().deleteHologramsInChunk(event.getChunk());
    }

}
