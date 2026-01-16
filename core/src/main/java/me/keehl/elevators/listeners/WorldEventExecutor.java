package me.keehl.elevators.listeners;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.helpers.ShulkerBoxHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorEventData;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.ElevatorDataContainerService;
import me.keehl.elevators.services.ElevatorHologramService;
import me.keehl.elevators.services.ElevatorSettingService;
import me.keehl.elevators.util.InternalElevatorSettingType;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
        handleExplosion(event.blockList());
    }

    public static void onBlockExplode(BlockExplodeEvent event) {
        handleExplosion(event.blockList());
    }

    private static void handleExplosion(List<Block> blockList) {
        for (int i = blockList.size() - 1; i >= 0; i--) {
            Block block = blockList.get(i);
            ShulkerBox shulkerBox = ShulkerBoxHelper.getShulkerBox(block);
            if (shulkerBox == null)
                continue;

            ElevatorType elevatorType = ElevatorHelper.getElevatorType(shulkerBox);
            if (elevatorType == null)
                continue;

            Elevator elevator = new Elevator(shulkerBox, elevatorType);

            blockList.remove(i);

            if (ElevatorSettingService.getElevatorSettingValue(elevator, InternalElevatorSettingType.CAN_EXPLODE)) {
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

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(event.getItem());
        if (elevatorType == null) return;
        if (!event.getBlock().getType().equals(Material.DISPENSER)) return;

        if (!ElevatorConfigService.getRootConfig().allowElevatorDispense) {
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

            ElevatorDataContainerService.updateTypeKeyOnElevator(box, elevatorType);
            ElevatorDataContainerService.dumpDataFromItemIntoShulkerBox(box, event.getItem());
            ElevatorHelper.onElevatorPlace(new Elevator(box, elevatorType));
            if (ElevatorConfigService.getRootConfig().forceFacingUpwards)
                ShulkerBoxHelper.setFacingUp(box);
        });
    }

    public static void onBlockBreak(BlockDropItemEvent event) {
        if (!(event.getBlockState() instanceof ShulkerBox)) return;
        ShulkerBox box = (ShulkerBox) event.getBlockState();

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box, false);
        if (elevatorType == null) return;

        Elevator elevator = new Elevator(box, elevatorType);
        ElevatorHologramService.deleteHologram(elevator);
        ItemStack newElevatorItem = ItemStackHelper.createItemStackFromElevator(elevator);

        Optional<Item> defaultItem = event.getItems().stream().filter(i -> !ItemStackHelper.isNotShulkerBox(i.getItemStack().getType())).findAny();
        if (!defaultItem.isPresent()) return;

        defaultItem.get().setItemStack(newElevatorItem);
    }

    public static void onBlockPlace(BlockPlaceEvent event) {
        //TODO: Check if elevator is disabled based on location for speed.

        ItemStack item = event.getItemInHand();
        Material type = item.getType();
        if (ItemStackHelper.isNotShulkerBox(type)) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(item);
        if (elevatorType == null) return;

        if (ElevatorConfigService.isWorldDisabled(event.getBlock().getWorld())) {
            event.setCancelled(true);
            MessageHelper.sendWorldDisabledMessage(event.getPlayer(), new ElevatorEventData(elevatorType));
            return;
        }
        int count = item.getAmount();
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            item.setAmount(count - 1);


        ShulkerBox box = ShulkerBoxHelper.getShulkerBox(event.getBlockPlaced());
        if (box == null)
            return;
        box = ElevatorDataContainerService.updateTypeKeyOnElevator(box, elevatorType);
        Elevator elevator = new Elevator(box, elevatorType);
        ElevatorHelper.onElevatorPlace(elevator);

        if (ElevatorConfigService.getRootConfig().forceFacingUpwards)
            ShulkerBoxHelper.setFacingUp(box);

        ElevatorHologramService.updateElevatorHologram(elevator);
    }


    public static void onChunkLoad(ChunkLoadEvent event) {
        if (!ElevatorHologramService.canUseHolograms())
            return;

        ElevatorHologramService.updateHologramsInChunk(event.getChunk());
    }

    public static void onChunkUnload(ChunkUnloadEvent event) {
        if (!ElevatorHologramService.canUseHolograms())
            return;

        ElevatorHologramService.deleteHologramsInChunk(event.getChunk());
    }

}
