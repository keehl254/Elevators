package me.keehl.elevators.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.lib.PaperLib;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.helpers.*;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorEventData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PaperEventExecutor {

    public static void onJump(PlayerJumpEvent event) {
        Block block = event.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        ShulkerBox box = ShulkerBoxHelper.getShulkerBox(block);
        if(box == null)
            return;

        IElevatorType elevatorType = ElevatorHelper.getElevatorType(box);
        if (elevatorType == null) return;

        if (Elevators.getConfigService().isWorldDisabled(event.getPlayer().getWorld())) {
            if (ElevatorHelper.hasOrAddPlayerCoolDown(event.getPlayer(), "message")) {
                Elevators.getLocale().getWorldDisabledMessage().sendFormatted(event.getPlayer(), new ElevatorEventData(event.getPlayer(), elevatorType));
            }
            return;
        }

        IElevator elevator = new Elevator(box, elevatorType);
        if(!Elevators.getHooksService().canUseElevator(event.getPlayer(), elevator, true))
            return;

        IElevatorEventData closest = ElevatorHelper.findDestinationElevator(event.getPlayer(), elevator, (byte) 1);
        if (closest == null) return;

        if (!ElevatorPermHelper.canUseElevator(event.getPlayer(), closest)) {
            if (ElevatorHelper.hasOrAddPlayerCoolDown(event.getPlayer(), "message")) {
                Elevators.getLocale().getCantUseMessage().sendFormatted(event.getPlayer(), closest);
            }
            return;
        }

        ElevatorHelper.onElevatorUse(event.getPlayer(), closest);
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

        IElevatorType elevatorType = ElevatorHelper.getElevatorType(event.getItem());
        if (elevatorType != null) {
            meta.setDisplayName(elevatorType.getDisplayName().toLegacyText());
            meta.setLore(elevatorType.getLore().stream().map(ILocaleComponent::toLegacyText).toList());
            event.getItem().setItemMeta(meta);
        }

        if(event.getSource().getType() == InventoryType.SHULKER_BOX && ElevatorHelper.isElevator(PaperLib.getHolder(event.getSource(), false).getHolder()))
            event.setCancelled(true);


        if(event.getDestination().getType() == InventoryType.SHULKER_BOX && ElevatorHelper.isElevator(PaperLib.getHolder(event.getDestination(), false).getHolder()))
            event.setCancelled(true);
    }

}
