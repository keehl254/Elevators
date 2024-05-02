package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.settings.ElevatorSetting;
import com.lkeehl.elevators.models.hooks.ProtectionHook;
import com.lkeehl.elevators.services.DataContainerService;
import com.lkeehl.elevators.services.HookService;
import com.lkeehl.elevators.services.interaction.SimpleDisplay;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.SignGUIBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class InventoryHelper {

    private static <T> Map<T, Integer> mapToInventorySlot(List<T> objects) {
        Map<T, Integer> objectMap = new HashMap<>();

        int inventorySize = objects.size() + (9 - (objects.size()) % 9);

        int numRows = (inventorySize / 9);
        int currentItemIndex = 0;
        for (int row = 0; row < numRows && currentItemIndex < objects.size(); row++) {
            int itemsToPlace = Math.min(9, objects.size() - currentItemIndex);

            int startColIndex = Math.max((9 - itemsToPlace) / 2, 0);

            for (int col = startColIndex; col < startColIndex + itemsToPlace; col++) {
                int slot = ((row + 1) * 9) + col;
                objectMap.put(objects.get(currentItemIndex), slot);
                currentItemIndex++;
            }
        }

        return objectMap;
    }

    private static Inventory createInventoryWithMinSlots(int minSlots, String title) {
        int inventorySize = minSlots + (9 - minSlots % 9);
        if (minSlots % 9 == 0)
            inventorySize -= 9;
        return Bukkit.createInventory(null, inventorySize, title);
    }

    private static void fillEmptySlotsWithPanes(Inventory inventory, DyeColor paneColor) {
        ItemStack pane = ItemStackHelper.createItem(" ", ItemStackHelper.getVariant(Material.BLACK_STAINED_GLASS_PANE, paneColor), 1);
        IntStream.range(0, inventory.getSize()).filter(i -> inventory.getItem(i) == null).forEach(i -> inventory.setItem(i, pane.clone()));
    }

    public static void openInteractMenu(Player player, Elevator elevator) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Elevator");

        InventoryHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        List<String> nameLore = new ArrayList<>();
        nameLore.add("");
        nameLore.add(ChatColor.GRAY + "Current Value: ");
        nameLore.add(ChatColor.GOLD + "" + ChatColor.BOLD + DataContainerService.getFloorName(elevator));

        ItemStack protectionItem = ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "Protection", Material.DIAMOND_SWORD, 1);
        ItemStack nameItem = ItemStackHelper.createItem(ChatColor.YELLOW + "" + ChatColor.BOLD + "Floor Name", Material.NAME_TAG, 1, nameLore);
        ItemStack settingsItem = ItemStackHelper.createItem(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Settings", Material.SEA_LANTERN, 1);

        List<ProtectionHook> protectionHooks = HookService.getProtectionHooks().stream().filter(i -> i.createIconForElevator(player, elevator) != null).toList();

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory);

        if (protectionHooks.size() == 0) {
            display.setItemSimple(11, nameItem, (event, myDisplay) -> InventoryHelper.openInteractNameMenu(player, elevator));
            display.setItemSimple(15, settingsItem, (event, myDisplay) -> InventoryHelper.openInteractSettingsMenu(player, elevator));
            display.open();
            return;
        }

        display.setItemSimple(10, protectionItem, (event, myDisplay) -> InventoryHelper.openInteractProtectMenu(player, elevator));
        display.setItemSimple(13, nameItem, (event, myDisplay) -> InventoryHelper.openInteractNameMenu(player, elevator));
        display.setItemSimple(16, settingsItem, (event, myDisplay) -> InventoryHelper.openInteractSettingsMenu(player, elevator));

        if (protectionHooks.size() == 1) {
            ProtectionHook hook = protectionHooks.get(0);
            ItemStack protectionIcon = hook.createIconForElevator(player, elevator);
            display.setItemSimple(10, protectionIcon, (event, myDisplay) -> hook.onProtectionClick(player, elevator, () -> openInteractMenu(player, elevator)));
        }

        display.open();
    }

    public static void openInteractProtectMenu(Player player, Elevator elevator) {

        List<ProtectionHook> protectionHooks = HookService.getProtectionHooks().stream().filter(i -> i.createIconForElevator(player, elevator) != null).toList();

        Inventory inventory = InventoryHelper.createInventoryWithMinSlots(protectionHooks.size() + 9, "Elevator > Protection");
        InventoryHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> openInteractMenu(player, elevator));
        for (Map.Entry<ProtectionHook, Integer> hookData : InventoryHelper.mapToInventorySlot(protectionHooks).entrySet()) {
            int slot = hookData.getValue() + 9;
            ItemStack icon = hookData.getKey().createIconForElevator(player, elevator);
            BiConsumer<InventoryClickEvent, SimpleDisplay> onClick = (event, myDisplay) -> hookData.getKey().onProtectionClick(player, elevator, () -> openInteractProtectMenu(player, elevator));
            display.setItemSimple(slot, icon, onClick);
        }

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));
        display.open();
    }

    public static void openInteractNameMenu(Player player, Elevator elevator) {
        Elevators.getElevatorsLogger().info("InteractName method called");

        String currentName = DataContainerService.getFloorName(elevator);
        try {
            SignGUIBuilder builder = SignGUI.builder();
            builder.setLines(currentName, ChatColor.BOLD + "^^^^^^^^", "Enter floor", "name above");
            builder.setHandler((p, result) -> {
                String newName = result.getLineWithoutColor(0).trim();
                return List.of(SignGUIAction.runSync(Elevators.getInstance(), () -> {
                    DataContainerService.setFloorName(elevator, newName.isEmpty() ? null : newName);
                    InventoryHelper.openInteractMenu(player, elevator);
                }));
            });

            builder.build().open(player);
        } catch (Exception e) {

        }
    }

    public static void openInteractSettingsMenu(Player player, Elevator elevator) {


        List<ElevatorSetting<?>> settings = new ArrayList<>();

    }

}
