package com.lkeehl.elevators.services.listeners;

import com.lkeehl.elevators.events.ElevatorUseEvent;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ElevatorPermHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.ElevatorSearchResult;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ConfigService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EntityEventExecutor {

    public void onJumpDefault(PlayerMoveEvent e) {

        if (e.getTo() == null || e.getPlayer().isFlying()) return;
        if (((Math.ceil((e.getFrom().getY() % 1) * 10000.0) / 10000.0) % 0.0625) != 0) return;
        if (e.getFrom().getY() >= e.getTo().getY()) return;
        if (((Math.ceil((e.getTo().getY() - e.getFrom().getY()) * 10000.0) / 10000.0) % 0.0625) == 0) return;

        Block block = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        if (!(block.getState() instanceof ShulkerBox)) return;

        ShulkerBox box = (ShulkerBox) block.getState();
        ElevatorType elevator = ElevatorHelper.getElevatorType(box.getBlock());
        if (elevator == null) return;

        BaseElevators.giveCooldown(e.getPlayer().getUniqueId());

        if (ConfigService.isWorldDisabled(e.getPlayer().getWorld())) {
            if (BaseElevators.hasNoCooldown(e.getPlayer().getUniqueId()))
                BaseUtil.sendMessage(e.getPlayer(), BaseElevators.locale.get("worldDisabledMessage"));
            return;
        }

        String accessMessage = BaseElevators.getTag().supportsProtection(box) ? GriefPrevention.canAccess(e.getPlayer(), block) : null;
        if (accessMessage != null) {
            if (BaseElevators.hasNoCooldown(e.getPlayer().getUniqueId()))
                e.getPlayer().sendMessage(ChatColor.RED + accessMessage);
            return;
        }

        ElevatorSearchResult closest = ElevatorHelper.findDestinationElevator(e.getPlayer(), box, elevator, (byte) 1);
        if (closest == null) return;

        if (!ElevatorPermHelper.canUseElevatorType(elevator, e.getPlayer(), box, (byte) 1)) {
            if (BaseElevators.hasNoCooldown(e.getPlayer().getUniqueId()))
                BaseUtil.sendMessage(e.getPlayer(), BaseElevators.locale.get("cantUseMessage"));
            return;
        }

        if (!elevator.allowsUse(e.getPlayer(), box, (byte) 1)) return;


        ElevatorUseEvent event = new ElevatorUseEvent(e.getPlayer(), box, closest);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        elevator.use(e.getPlayer(), box, closest.getKey(), closest.getValue(), (byte) 1);
    }

    public void onSneak(PlayerToggleSneakEvent e) {
        if (!e.isSneaking()) return;

        Block block = e.getPlayer().getLocation().getBlock().getLocation().subtract(0, 1, 0).getBlock();
        if (!(block.getState() instanceof ShulkerBox box)) return;

        ElevatorType elevator = ElevatorHelper.getElevatorType(box.getBlock());
        if (elevator == null) return;

        if (ConfigService.isWorldDisabled(e.getPlayer().getWorld())) {
            BaseUtil.sendMessage(e.getPlayer(), BaseElevators.locale.get("worldDisabledMessage"));
            return;
        }

        String accessMessage = BaseElevators.getTag().supportsProtection(box) ? GriefPrevention.canAccess(e.getPlayer(), block) : null;
        if (accessMessage != null) {
            e.getPlayer().sendMessage(ChatColor.RED + accessMessage);
            return;
        }

        ElevatorSearchResult closest = ElevatorHelper.findDestinationElevator(e.getPlayer(), box, elevator, (byte) -1);
        if (closest == null) return;

        if (!ElevatorPermHelper.canUseElevatorType(elevator, e.getPlayer(), box, (byte) -1)) {
            if (BaseElevators.hasNoCooldown(e.getPlayer().getUniqueId())) {
                BaseUtil.sendMessage(e.getPlayer(), BaseElevators.locale.get("cantUseMessage"));
                BaseElevators.giveCooldown(e.getPlayer().getUniqueId());
            }
            return;
        }

        if (!elevator.allowsUse(e.getPlayer(), box, (byte) -1)) return;

        ElevatorUseEvent event = new ElevatorUseEvent(e.getPlayer(), box, closest);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        elevator.use(e.getPlayer(), box, closest.getKey(), closest.getValue(), (byte) -1);
    }

    public void onPickup(EntityPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();
        if (ItemStackHelper.isNotShulkerBox(item.getType())) return;
        if (!ElevatorHelper.isElevator(item)) return;
        if (!(e.getEntity() instanceof Player player)) return;

        Inventory inv = player.getInventory();

        e.setCancelled(true);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
        BaseUtil.giveElevator(e.getItem(), inv);

        //TODO: With the new drop system, this may no longer be required. I hope not. A more natural pickup would be perfect.
    }

}
