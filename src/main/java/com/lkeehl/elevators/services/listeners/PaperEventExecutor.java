package com.lkeehl.elevators.services.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.lkeehl.elevators.events.ElevatorUseEvent;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ElevatorPermHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.settings.DisplayNameSetting;
import com.lkeehl.elevators.models.settings.LoreLinesSetting;
import com.lkeehl.elevators.services.ElevatorConfigService;
import com.lkeehl.elevators.services.ElevatorSettingService;
import com.lkeehl.elevators.services.ElevatorHookService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PaperEventExecutor {

    public static void onJump(PlayerJumpEvent e) {
        Block block = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        if (!(block.getState() instanceof ShulkerBox box)) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box.getBlock());
        if (elevatorType == null) return;

        if (ElevatorConfigService.isWorldDisabled(e.getPlayer().getWorld())) {
            if (ElevatorHelper.hasOrAddPlayerCoolDown(e.getPlayer(), "message"))
                MessageHelper.sendWorldDisabledMessage(e.getPlayer(), new ElevatorEventData(elevatorType));
            return;
        }

        Elevator elevator = new Elevator(box, elevatorType);
        if(!ElevatorHookService.canUseElevator(e.getPlayer(), elevator, true))
            return;

        ElevatorEventData closest = ElevatorHelper.findDestinationElevator(e.getPlayer(), elevator, (byte) 1);
        if (closest == null) return;

        if (!ElevatorPermHelper.canUseElevator(e.getPlayer(), closest)) {
            if (ElevatorHelper.hasOrAddPlayerCoolDown(e.getPlayer(), "message"))
                MessageHelper.sendCantUseMessage(e.getPlayer(), closest);
            return;
        }

        ElevatorUseEvent event = new ElevatorUseEvent(e.getPlayer(), box, closest);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        ElevatorHelper.onElevatorUse(e.getPlayer(), closest);
    }

    // Improvement made here is the use of getHolder without snapshots. Man I love paper.
    public static void onHopperTake(InventoryMoveItemEvent event) {
        if(event.getSource().getType() != InventoryType.SHULKER_BOX && event.getDestination().getType() != InventoryType.SHULKER_BOX)
            return;

        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return;

        if (item.getType().equals(Material.COMMAND_BLOCK) && (meta.getDisplayName().equalsIgnoreCase("elevator"))) {
            event.setCancelled(true);
            return;
        }

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(event.getItem());
        if (elevatorType != null) {
            meta.setDisplayName(MessageHelper.formatColors(ElevatorSettingService.getSettingValue(elevatorType, DisplayNameSetting.class)));
            meta.setLore(MessageHelper.formatColors(ElevatorSettingService.getSettingValue(elevatorType, LoreLinesSetting.class)));
            event.getItem().setItemMeta(meta);
        }

        if (event.getSource().getType() == InventoryType.SHULKER_BOX && event.getSource().getHolder(false) instanceof ShulkerBox fromBox) {
            if (ElevatorHelper.isElevator(fromBox))
                event.setCancelled(true);
        }

        if (event.getDestination().getType() == InventoryType.SHULKER_BOX && event.getDestination().getHolder(false) instanceof ShulkerBox toBox) {
            if (ElevatorHelper.isElevator(toBox))
                event.setCancelled(true);
        }
    }

}
