package com.lkeehl.elevators.services.listeners;

import com.lkeehl.elevators.events.ElevatorUseEvent;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ElevatorPermHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.services.HookService;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class EntityEventExecutor {

    public static void onJumpDefault(PlayerMoveEvent e) {

        if (e.getTo() == null || e.getPlayer().isFlying()) return;
        if (((Math.ceil((e.getFrom().getY() % 1) * 10000.0) / 10000.0) % 0.0625) != 0) return;
        if (e.getFrom().getY() >= e.getTo().getY()) return;
        if (((Math.ceil((e.getTo().getY() - e.getFrom().getY()) * 10000.0) / 10000.0) % 0.0625) == 0) return;

        Block block = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        if (!(block.getState() instanceof ShulkerBox box)) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box.getBlock());
        if (elevatorType == null) return;

        if (ElevatorHelper.hasOrAddPlayerCoolDown(e.getPlayer(), "use"))
            return;

        if (ConfigService.isWorldDisabled(e.getPlayer().getWorld())) {
            if (ElevatorHelper.hasOrAddPlayerCoolDown(e.getPlayer(), "message"))
                MessageHelper.sendWorldDisabledMessage(e.getPlayer(), new ElevatorEventData(elevatorType));
            return;
        }

        Elevator elevator = new Elevator(box, elevatorType);
        if(!HookService.canUseElevator(e.getPlayer(), elevator, true))
            return;

        ElevatorEventData closest = ElevatorHelper.findDestinationElevator(e.getPlayer(), box, elevatorType, (byte) 1);
        if (closest == null) return;

        if (!ElevatorPermHelper.canUseElevator(e.getPlayer(), elevator, (byte) 1)) {
            if (ElevatorHelper.hasOrAddPlayerCoolDown(e.getPlayer(), "message"))
                MessageHelper.sendCantUseMessage(e.getPlayer(), closest);
            return;
        }

        ElevatorUseEvent event = new ElevatorUseEvent(e.getPlayer(), box, closest);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        ElevatorHelper.onElevatorUse(e.getPlayer(), closest);
    }

    public static void onSneak(PlayerToggleSneakEvent e) {
        if (!e.isSneaking()) return;

        Block block = e.getPlayer().getLocation().getBlock().getLocation().subtract(0, 1, 0).getBlock();
        if (!(block.getState() instanceof ShulkerBox box)) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box.getBlock());
        if (elevatorType == null) return;

        if (ConfigService.isWorldDisabled(e.getPlayer().getWorld())) {
            MessageHelper.sendWorldDisabledMessage(e.getPlayer(), new ElevatorEventData(elevatorType));
            return;
        }

        Elevator elevator = new Elevator(box, elevatorType);
        if(!HookService.canUseElevator(e.getPlayer(), elevator, true))
            return;

        ElevatorEventData closest = ElevatorHelper.findDestinationElevator(e.getPlayer(), box, elevatorType, (byte) -1);
        if (closest == null) return;

        if (!ElevatorPermHelper.canUseElevator(e.getPlayer(), elevator, (byte) -1)) {
            if (ElevatorHelper.hasOrAddPlayerCoolDown(e.getPlayer(), "message"))
                MessageHelper.sendCantUseMessage(e.getPlayer(), closest);
            return;
        }

        ElevatorUseEvent event = new ElevatorUseEvent(e.getPlayer(), box, closest);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        ElevatorHelper.onElevatorUse(e.getPlayer(), closest);
    }

/*    public void onPickup(EntityPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();
        if (ItemStackHelper.isNotShulkerBox(item.getType())) return;
        if (!ElevatorHelper.isElevator(item)) return;
        if (!(e.getEntity() instanceof Player player)) return;

        Inventory inv = player.getInventory();

        e.setCancelled(true);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
        BaseUtil.giveElevator(e.getItem(), inv);

        TODO: With the new drop system, this may no longer be required. I hope not. A more natural pickup would be perfect.
    }*/

}
