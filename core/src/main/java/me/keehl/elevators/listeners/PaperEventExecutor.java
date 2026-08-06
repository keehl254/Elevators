package me.keehl.elevators.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.ElevatorPermHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.helpers.ShulkerBoxHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorEventData;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.ElevatorSettingService;
import me.keehl.elevators.services.ElevatorHookService;
import io.github.rvskele.paperlib.PaperLib;
import me.keehl.elevators.util.InternalElevatorSettingType;
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
        ShulkerBox box = ShulkerBoxHelper.getShulkerBox(block);
        if(box == null)
            return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box);
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
            meta.setDisplayName(MessageHelper.formatLineColors(ElevatorSettingService.getElevatorSettingValue(elevatorType, InternalElevatorSettingType.DISPLAY_NAME)));
            meta.setLore(MessageHelper.formatListColors(ElevatorSettingService.getElevatorSettingValue(elevatorType, InternalElevatorSettingType.LORE_LINES)));
            event.getItem().setItemMeta(meta);
        }

        if(event.getSource().getType() == InventoryType.SHULKER_BOX && ElevatorHelper.isElevator(PaperLib.getHolder(event.getSource(), false).getHolder()))
            event.setCancelled(true);


        if(event.getDestination().getType() == InventoryType.SHULKER_BOX && ElevatorHelper.isElevator(PaperLib.getHolder(event.getDestination(), false).getHolder()))
            event.setCancelled(true);
    }

}
