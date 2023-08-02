package com.lkeehl.elevators.services.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
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

public class PaperEventExecutor {

    public static void onJump(PlayerJumpEvent e) {
        Block block = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        if (!(block.getState() instanceof ShulkerBox box)) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box.getBlock());
        if (elevatorType == null) return;

        if (!ElevatorHelper.hasOrAddPlayerCoolDown(e.getPlayer(), "use"))
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

}
