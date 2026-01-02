package me.keehl.elevators.menus.interact;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ElevatorMenuHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.ProtectionHook;
import me.keehl.elevators.services.ElevatorHookService;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class InteractProtectMenu {

    public static void openInteractProtectMenu(Player player, Elevator elevator) {
        if (!elevator.isValid()) {
            InteractMenu.openInteractMenu(player, elevator);
            return;
        }

        List<ProtectionHook> protectionHooks = ElevatorHookService.getProtectionHooks().stream().filter(i -> i.getConfig().allowCustomization).filter(i -> i.createIconForElevator(player, elevator) != null).collect(Collectors.toList());

        Inventory inventory = ElevatorMenuHelper.createInventoryWithMinSlots(protectionHooks.size() + 9, "Elevator > Protection");
        ElevatorMenuHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> InteractMenu.openInteractMenu(player, elevator));
        for (Map.Entry<ProtectionHook, Integer> hookData : ElevatorMenuHelper.mapToInventorySlot(protectionHooks).entrySet()) {
            int slot = hookData.getValue() + 9;
            ItemStack icon = hookData.getKey().createIconForElevator(player, elevator);
            BiConsumer<InventoryClickEvent, SimpleDisplay> onClick = (event, myDisplay) -> {
                myDisplay.stopReturn();
                hookData.getKey().onProtectionClick(player, elevator, () -> InteractProtectMenu.openInteractProtectMenu(player, elevator));
            };
            display.setItemSimple(slot, icon, onClick);
        }

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));
        display.open();
    }

}
