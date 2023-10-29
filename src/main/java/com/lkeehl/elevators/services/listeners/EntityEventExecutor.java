package com.lkeehl.elevators.services.listeners;

import com.lkeehl.elevators.events.ElevatorUseEvent;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.ElevatorPermHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.services.ElevatorRecipeService;
import com.lkeehl.elevators.services.HookService;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Random;

public class EntityEventExecutor {

    private static final Random random = new Random();

    public static void onJoin(PlayerJoinEvent e) {
        ElevatorRecipeService.discoverRecipesForPlayer(e.getPlayer());
    }
    public static void onJumpDefault(PlayerMoveEvent e) {

        if (e.getPlayer().isFlying()) return;
        if (((Math.ceil((e.getFrom().getY() % 1) * 10000.0) / 10000.0) % 0.0625) != 0) return;
        if (e.getFrom().getY() >= e.getTo().getY()) return;
        if (((Math.ceil((e.getTo().getY() - e.getFrom().getY()) * 10000.0) / 10000.0) % 0.0625) == 0) return;

        Block block = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        if (!(block.getState() instanceof ShulkerBox box)) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(box.getBlock());
        if (elevatorType == null) return;

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

    public static void onPickup(EntityPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();
        if (ItemStackHelper.isNotShulkerBox(item.getType())) return;
        if (!ElevatorHelper.isElevator(item)) return;
        if (!(e.getEntity() instanceof Player player)) return;

        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta == null) return; // To appease the god that is intellisense.

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(item);
        if(elevatorType.getMaxStackSize() <= 1) return; // I really wish Minecraft would support custom item maxStackSizes already. Returning here gives the most natural pickup.

        int pickupAmount = item.getAmount();

        Map<ItemStack, Integer> leftoverItems = ItemStackHelper.addElevatorToInventory(elevatorType, item.getAmount(), item.getType(), player.getInventory(), itemMeta.getDisplayName(), itemMeta.getLore());

        if(leftoverItems.size() >= 1) { // The itemstack helper will add to all possible places. If it failed, there is no space. This event shouldn't fire again if that is the case.
            int itemsLeft = leftoverItems.values().stream().mapToInt(i->i).sum();
            item.setAmount(itemsLeft);

            e.setCancelled(true);
            return;
        }
        // Our itemstack helper managed to add all the items :)
        e.setCancelled(true);
        player.incrementStatistic(Statistic.PICKUP, item.getType(), pickupAmount); // Custom statistics for elevators might be nice, too :P
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);

        e.getItem().remove();


      /*  if(MCVersionHelper.doesVersionSupportPaperCollectEffect()) // Has built in paper check.
            player.playPickupItemAnimation(e.getItem()); // Praise paper and their paper ways.*/

    }

}
