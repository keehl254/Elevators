package com.lkeehl.elevators.services.listeners;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.helpers.ShulkerBoxHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.settings.CanExplodeSetting;
import com.lkeehl.elevators.services.ElevatorConfigService;
import com.lkeehl.elevators.services.ElevatorDataContainerService;
import com.lkeehl.elevators.services.ElevatorHologramService;
import com.lkeehl.elevators.services.ElevatorSettingService;
import org.bukkit.GameMode;
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

            ElevatorType elevatorType = ElevatorHelper.getElevatorType(shulkerBox);
            if (elevatorType == null) continue;

            if (ElevatorSettingService.getSettingValue(new Elevator(shulkerBox, elevatorType), CanExplodeSetting.class))
                event.blockList().remove(block);
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
        if (!(event.getBlockState() instanceof ShulkerBox box)) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box, false);
        if (elevatorType == null) return;

        Elevator elevator = new Elevator(box, elevatorType);
        ElevatorHologramService.deleteHologram(elevator);
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
