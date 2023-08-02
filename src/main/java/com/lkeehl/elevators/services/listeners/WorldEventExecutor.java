package com.lkeehl.elevators.services.listeners;

import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.helpers.ShulkerBoxHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.services.DataContainerService;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Item;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
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
        for (int i = 0; i < event.blockList().size(); i++) {
            Block block = event.blockList().get(i);
            if (!(block.getState() instanceof ShulkerBox))
                continue;
            ElevatorType elevator = ElevatorHelper.getElevatorType(block);
            if (elevator != null && !elevator.canElevatorExplode())
                event.blockList().remove(block);
        }
    }

    public static void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.isBlockInHand() && event.getPlayer().isSneaking()) return;
        if (event.getClickedBlock() == null) return;
        if (!(event.getClickedBlock().getState() instanceof ShulkerBox box)) return;
        if (!ElevatorHelper.isElevator(box)) return;

        event.setCancelled(true);
        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box);

        if (event.getPlayer().isSneaking()) {
            if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;

            ElevatorHelper.onElevatorInteract(event.getPlayer(), event, new Elevator(box,elevatorType));
        }
    }

    public static void onDispenserPlace(BlockDispenseEvent event) {
        if (ItemStackHelper.isNotShulkerBox(event.getItem().getType())) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(event.getItem());
        if (elevatorType == null) return;
        if (!event.getBlock().getType().equals(Material.DISPENSER)) return;

        Dispenser dispenser = (Dispenser) event.getBlock().getBlockData();
        Block relative = event.getBlock().getRelative(dispenser.getFacing());
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Elevators")), () -> {
            if (!(relative.getState() instanceof ShulkerBox box)) return;

            box = DataContainerService.updateTypeKeyOnElevator(box, elevatorType);
            DataContainerService.dumpDataFromItemIntoShulkerBox(box, event.getItem());
            ElevatorHelper.onElevatorPlace(new Elevator(box,elevatorType));
            if (ConfigService.getRootConfig().forceFacingUpwards)
                ShulkerBoxHelper.setFacingUp(box);
        });
    }

    public static void onBlockBreak(BlockDropItemEvent event) {
        if (!(event.getBlockState() instanceof ShulkerBox box)) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box);
        if (elevatorType == null) return;

        ItemStack newElevatorItem = ItemStackHelper.createItemStackFromElevator(new Elevator(box, elevatorType));

        Optional<Item> defaultItem = event.getItems().stream().filter(ElevatorHelper::isElevator).findAny();
        if(defaultItem.isEmpty()) return;

        defaultItem.get().setItemStack(newElevatorItem);
    }

    public static void onBlockPlace(BlockPlaceEvent event) {
        //TODO: Check if elevator is disabled based on location for speed.

        ItemStack item = event.getItemInHand();
        Material type = item.getType();
        if (ItemStackHelper.isNotShulkerBox(type)) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(item);
        if (elevatorType == null) return;

        if (ConfigService.isWorldDisabled(event.getBlock().getWorld())) {
            event.setCancelled(true);
            MessageHelper.sendWorldDisabledMessage(event.getPlayer(), new ElevatorEventData(elevatorType));
            return;
        }
        int count = item.getAmount();
        if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            item.setAmount(count - 1);

        ShulkerBox box = DataContainerService.updateTypeKeyOnElevator((ShulkerBox) event.getBlockPlaced().getState(), elevatorType);
        ElevatorHelper.onElevatorPlace(new Elevator(box,elevatorType));

        if (ConfigService.getRootConfig().forceFacingUpwards)
            ShulkerBoxHelper.setFacingUp(box);
    }







}
