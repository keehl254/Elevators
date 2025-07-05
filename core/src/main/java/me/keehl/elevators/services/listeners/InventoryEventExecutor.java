package me.keehl.elevators.services.listeners;

import me.keehl.elevators.helpers.*;
import me.keehl.elevators.models.ElevatorEventData;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.settings.DisplayNameSetting;
import me.keehl.elevators.models.settings.LoreLinesSetting;
import me.keehl.elevators.models.settings.MaxStackSizeSetting;
import me.keehl.elevators.services.ElevatorDataContainerService;
import me.keehl.elevators.services.ElevatorSettingService;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryEventExecutor {

    public static void onInventoryOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof ShulkerBox)) return;
        ShulkerBox box = (ShulkerBox) holder;

        if (!ElevatorHelper.isElevator(box)) return;

        event.setCancelled(true);
        event.getPlayer().closeInventory();

        ShulkerBoxHelper.playClose(box);
    }

    public static void updateStackOnClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        ElevatorType elevatorType = ElevatorHelper.getElevatorType(clickedItem);
        if(elevatorType == null || clickedItem == null)
            return;

        ItemStackHelper.updateElevator(elevatorType, clickedItem);
    }

    @SuppressWarnings("ConstantConditions")
    public static void onClickStackHandler(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        ItemStack clickedItem = event.getCurrentItem();

        if (event.getCursor() == null || event.getCursor().getType().isAir()) { // Only for halving elevators to cursor.
            if (event.getClick() != ClickType.RIGHT) return;

            if (clickedItem == null) return;
            if (ItemStackHelper.isNotShulkerBox(clickedItem.getType())) return;
            if (!ElevatorHelper.isElevator(clickedItem)) return;

            event.setCancelled(true);

            int currentLeftSize = (clickedItem.getAmount() - (clickedItem.getAmount() % 2)) / 2; // Change size of the current item rather than cursor to mimic MC behavior.

            ItemStack cursorItem = clickedItem.clone();
            cursorItem.setAmount(cursorItem.getAmount() - currentLeftSize);

            clickedItem.setAmount(currentLeftSize);
            player.setItemOnCursor(cursorItem);
            return;
        }
        if (event.getClick() != ClickType.LEFT) return; // Dropping outside still counts.
        if (event.getClickedInventory() == null) return; // In case they drop it outside their inventory.
        if (ItemStackHelper.isNotShulkerBox(event.getCursor().getType())) return;
        if (!ElevatorHelper.isElevator(event.getCursor())) return;


        if (clickedItem == null) {
            event.setCancelled(true);
            return;
        }

        // Adding cursor to clicked stack.
        if (!clickedItem.getType().isAir() && clickedItem instanceof ShulkerBox) {
            ElevatorType elevatorType = ElevatorHelper.getElevatorType(clickedItem);
            int amountToAdd = ElevatorSettingService.getSettingValue(elevatorType, MaxStackSizeSetting.class) - clickedItem.getAmount();
            amountToAdd = Math.min(amountToAdd, event.getCursor().getAmount());

            clickedItem.setAmount(clickedItem.getAmount() + amountToAdd);
            event.getCursor().setAmount(event.getCursor().getAmount() - amountToAdd);
        }
    }

    public static void onHopperTake(InventoryMoveItemEvent event) {
        if (event.getSource().getType() != InventoryType.SHULKER_BOX && event.getDestination().getType() != InventoryType.SHULKER_BOX)
            return;
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
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
        Location src = event.getSource().getLocation();
        if (event.getSource().getType() == InventoryType.SHULKER_BOX && src != null && !ItemStackHelper.isNotShulkerBox(src.getBlock().getType())) {
            if (ElevatorHelper.isElevator(src.getBlock()))
                event.setCancelled(true);
        }
        Location dst = event.getDestination().getLocation();
        if (event.getDestination().getType() == InventoryType.SHULKER_BOX && dst != null && !ItemStackHelper.isNotShulkerBox(dst.getBlock().getType())) {
            if (ElevatorHelper.isElevator(dst.getBlock()))
                event.setCancelled(true);
        }
    }

    public static void onAnvilPrepare(PrepareAnvilEvent e) {
        AnvilInventory inventory = e.getInventory();
        ItemStack item = inventory.getItem(0);
        if (item == null)
            return;
        ElevatorType type = ElevatorHelper.getElevatorType(item);
        if (type == null)
            return;

        if (e.getResult() == null)
            return;

        ItemStack result = e.getResult().clone();
        if (result.getType().isAir() || ItemStackHelper.isNotShulkerBox(result.getType()))
            return;
        ItemStack newElevator = new ItemStack(result.getType(), result.getAmount());
        ElevatorDataContainerService.dumpDataFromItemIntoItem(item, newElevator);

        ItemMeta meta = newElevator.getItemMeta();
        if (meta != null && result.getItemMeta() != null && result.getItemMeta().hasDisplayName())
            meta.setDisplayName(result.getItemMeta().getDisplayName());
        newElevator.setItemMeta(meta);
        e.setResult(newElevator);
    }

    public static void onCraft(CraftItemEvent e) {
        if (!(e.getRecipe() instanceof Keyed)) return;
        Keyed keyedRecipe = (Keyed) e.getRecipe();

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();

        ItemStack result = e.getInventory().getResult();
        if (result == null || result.getType() == Material.AIR) return;
        if (ItemStackHelper.isNotShulkerBox(result.getType())) return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(result);
        if (elevatorType == null) return;

        boolean isElevatorCraftingRecipe = keyedRecipe.getKey().getNamespace().equalsIgnoreCase("elevators");
        DyeColor dyeColor = ItemStackHelper.getDyeColorFromMaterial(result.getType());

        if (isElevatorCraftingRecipe) {

            // There is no need to pass in the color as the recipe can only represent one color anyway.
            if (!ElevatorPermHelper.canCraftElevatorType(elevatorType, player, (Recipe & Keyed) e.getRecipe())) {
                MessageHelper.sendCantCreateMessage(player, new ElevatorEventData(elevatorType));
                e.setCancelled(true);
            }
        } else if (!ElevatorPermHelper.canDyeElevatorType(elevatorType, player, dyeColor)) {
            MessageHelper.sendCantDyeMessage(player, new ElevatorEventData(elevatorType));
            e.setCancelled(true);
        }
    }

}
