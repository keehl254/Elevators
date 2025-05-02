package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.settings.ElevatorSetting;
import com.lkeehl.elevators.models.hooks.ProtectionHook;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.services.DataContainerService;
import com.lkeehl.elevators.services.ElevatorSettingService;
import com.lkeehl.elevators.services.HookService;
import com.lkeehl.elevators.services.interaction.SimpleDisplay;
import com.lkeehl.elevators.services.interaction.SimpleInput;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.SignGUIBuilder;
import de.rapha149.signgui.exception.SignGUIVersionException;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
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

    private static void openMenuFromDisplay(SimpleDisplay myDisplay, Player player, Elevator elevator, BiConsumer<Player, Elevator> newMethod) {
        myDisplay.stopReturn();
        newMethod.accept(player, elevator);
    }

    public static void openInteractMenu(Player player, Elevator elevator) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Elevator");

        ElevatorHelper.setElevatorDisabled(elevator.getShulkerBox());
        ShulkerBoxHelper.playOpen(elevator.getShulkerBox());

        InventoryHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        List<String> nameLore = new ArrayList<>();
        nameLore.add("");
        nameLore.add(ChatColor.GRAY + "Current Value: ");
        nameLore.add(ChatColor.GOLD + "" + ChatColor.BOLD + DataContainerService.getFloorName(elevator));

        ItemStack protectionItem = ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "Protection", Material.DIAMOND_SWORD, 1);
        ItemStack nameItem = ItemStackHelper.createItem(ChatColor.YELLOW + "" + ChatColor.BOLD + "Floor Name", Material.NAME_TAG, 1, nameLore);
        ItemStack settingsItem = ItemStackHelper.createItem(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Settings", Material.SEA_LANTERN, 1);

        List<ProtectionHook> protectionHooks = HookService.getProtectionHooks().stream().filter(i -> i.getConfig().allowCustomization).filter(i -> i.createIconForElevator(player, elevator) != null).toList();

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> {
            ElevatorHelper.setElevatorEnabled(elevator.getShulkerBox());
            ShulkerBoxHelper.playClose(elevator.getShulkerBox());
        });

        boolean canRename = HookService.canRenameElevator(player, elevator, false);

        if (protectionHooks.isEmpty()) {

            if(canRename) {
                display.setItemSimple(11, nameItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InventoryHelper::openInteractNameMenu));
                display.setItemSimple(15, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InventoryHelper::openInteractSettingsMenu));
            } else {
                display.setItemSimple(13, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InventoryHelper::openInteractSettingsMenu));
            }
            display.open();
            return;
        }

        if(canRename) {
            display.setItemSimple(10, protectionItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InventoryHelper::openInteractProtectMenu));
            display.setItemSimple(13, nameItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InventoryHelper::openInteractNameMenu));
            display.setItemSimple(16, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InventoryHelper::openInteractSettingsMenu));
        } else {
            display.setItemSimple(11, protectionItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InventoryHelper::openInteractProtectMenu));
            display.setItemSimple(15, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InventoryHelper::openInteractSettingsMenu));
        }

        if (protectionHooks.size() == 1) {
            ProtectionHook hook = protectionHooks.getFirst();
            ItemStack protectionIcon = hook.createIconForElevator(player, elevator);
            display.setItemSimple(canRename ? 10 : 11, protectionIcon, (event, myDisplay) -> {
                myDisplay.stopReturn();
                hook.onProtectionClick(player, elevator, () -> openInteractMenu(player, elevator));
            });
        }

        display.open();
    }

    public static void openInteractProtectMenu(Player player, Elevator elevator) {

        List<ProtectionHook> protectionHooks = HookService.getProtectionHooks().stream().filter(i -> i.getConfig().allowCustomization).filter(i -> i.createIconForElevator(player, elevator) != null).toList();

        Inventory inventory = InventoryHelper.createInventoryWithMinSlots(protectionHooks.size() + 9, "Elevator > Protection");
        InventoryHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> openInteractMenu(player, elevator));
        for (Map.Entry<ProtectionHook, Integer> hookData : InventoryHelper.mapToInventorySlot(protectionHooks).entrySet()) {
            int slot = hookData.getValue() + 9;
            ItemStack icon = hookData.getKey().createIconForElevator(player, elevator);
            BiConsumer<InventoryClickEvent, SimpleDisplay> onClick = (event, myDisplay) -> {
                myDisplay.stopReturn();
                hookData.getKey().onProtectionClick(player, elevator, () -> openInteractProtectMenu(player, elevator));
            };
            display.setItemSimple(slot, icon, onClick);
        }

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));
        display.open();
    }

    public static void openInteractNameMenu(Player player, Elevator elevator) {
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

            player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
            builder.build().open(player);
        } catch (SignGUIVersionException e) {
            player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

            SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
            input.allowReset();
            input.onComplete(newName -> {
                DataContainerService.setFloorName(elevator, newName);
                InventoryHelper.openInteractMenu(player, elevator);
                return SimpleInput.SimpleInputResult.STOP;
            });
            input.onCancel(() -> {
                InventoryHelper.openInteractMenu(player, elevator);
            });
            MessageHelper.sendFormattedMessage(player, ConfigService.getRootConfig().locale.enterFloorName);
            input.start();
        }
    }

    private static List<ElevatorAction> getActionsWithSettings(Elevator elevator, boolean up) {
        List<ElevatorAction> actions = new ArrayList<>(up ? elevator.getElevatorType().getActionsUp() : elevator.getElevatorType().getActionsDown()); // Don't want to alter original list.
        actions.removeIf(i -> i.getSettings().isEmpty());
        actions.removeIf(i -> i.getSettings().stream().noneMatch(s -> s.canBeEditedIndividually(elevator)));
        return actions;
    }

    public static void openInteractActionSettingsMenu(Player player, Elevator elevator, ElevatorAction action, Runnable onReturn) {

        List<ElevatorActionSetting<?>> settings = new ArrayList<>(action.getSettings());
        settings.removeIf(i -> !i.canBeEditedIndividually(elevator));

        int inventorySize = (Math.floorDiv(settings.size() + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Settings > Actions > Action");
        InventoryHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory);
        action.onStartEditing(player, display, elevator);
        display.onReturn(() -> {
            action.onStopEditing(player, display, elevator);
            onReturn.run();
        });

        for(int i=0;i< settings.size();i++) {
            ElevatorActionSetting<?> setting = settings.get(i);
            display.setItemSimple(i+9, setting.createIcon(setting.getIndividualElevatorValue(elevator), false), (event, myDisplay) -> {
                myDisplay.stopReturn();
                setting.clickIndividual(player, elevator, () -> openInteractActionSettingsMenu(player, elevator, action, onReturn), event);
            });
        }
        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));
        display.open();
    }

    public static void openInteractActionsMenu(Player player, Elevator elevator, List<ElevatorAction> actions) {

        int inventorySize = (Math.floorDiv(actions.size() + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Elevator > Settings > Actions");
        InventoryHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> openInteractSettingsMenu(player, elevator));
        for(int i=0;i< actions.size();i++) {
            ElevatorAction action = actions.get(i);
            display.setItemSimple(i+9, action.getIcon(), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openInteractActionSettingsMenu(player, elevator, action, () -> openInteractActionsMenu(player, elevator, actions));
            });
        }
        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));
        display.open();
    }

    public static void openInteractSettingsMenu(Player player, Elevator elevator) {
        List<ElevatorSetting<?>> settings = ElevatorSettingService.getElevatorSettings().stream().filter(i -> i.canBeEditedIndividually(elevator)).toList();

        int itemAmount = settings.size();
        List<ElevatorAction> upActions = getActionsWithSettings(elevator, true);
        List<ElevatorAction> downActions = getActionsWithSettings(elevator, false);

        if(!upActions.isEmpty())
            itemAmount++;
        if(!downActions.isEmpty())
            itemAmount++;

        int inventorySize = (Math.floorDiv(itemAmount + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Elevator > Settings");
        InventoryHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> openInteractMenu(player, elevator));
        for(int i=0;i< settings.size();i++) {
            ElevatorSetting<?> setting = settings.get(i);
            display.setItemSimple(i+9, setting.createIcon(setting.getIndividualElevatorValue(elevator), false), (event, myDisplay) -> {
                myDisplay.stopReturn();
                setting.clickIndividual(player, elevator, () -> openInteractSettingsMenu(player, elevator), event);
            });
        }

        if(!downActions.isEmpty()) {
            display.setItemSimple(inventory.getSize() - 1, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Downwards Actions", Material.SPECTRAL_ARROW, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openInteractActionsMenu(player, elevator, downActions);
            });
        }

        if(!upActions.isEmpty()) {
            display.setItemSimple(inventory.getSize() - (downActions.isEmpty() ? 1 : 2), ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Upwards Actions", Material.TIPPED_ARROW, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openInteractActionsMenu(player, elevator, upActions);
            });
        }

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));

        display.open();
    }

}
