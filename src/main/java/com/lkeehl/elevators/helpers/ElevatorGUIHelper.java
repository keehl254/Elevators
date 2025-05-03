package com.lkeehl.elevators.helpers;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.settings.ElevatorSetting;
import com.lkeehl.elevators.models.hooks.ProtectionHook;
import com.lkeehl.elevators.services.*;
import com.lkeehl.elevators.services.interaction.PagedDisplay;
import com.lkeehl.elevators.services.interaction.SimpleDisplay;
import com.lkeehl.elevators.services.interaction.SimpleInput;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.SignGUIBuilder;
import de.rapha149.signgui.exception.SignGUIVersionException;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.version.VersionMatcher;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation") public class ElevatorGUIHelper {

    private static boolean anvilEnabled;
    private static boolean signEnabled;

    static {
        try {
            (new VersionMatcher()).match();
            anvilEnabled = true;
        }catch (Exception ex){
            anvilEnabled = false;
        }
        try {
            SignGUI.builder();
            signEnabled = true;
        } catch (SignGUIVersionException e) {
            signEnabled = false;
        }


    }

    public static void tryOpenSign(Player player, Function<String, Boolean> validationFunction, Consumer<String> resultConsumer, Runnable onCancel, String inputMessage, boolean allowReset, String... lines) {
        if(signEnabled){
            try {
                SignGUIBuilder builder = SignGUI.builder();
                builder.setLines(lines);
                builder.setHandler((p, result) -> {
                    String input = result.getLineWithoutColor(0).trim();
                    if (allowReset && (input.isEmpty() || input.equalsIgnoreCase("reset")))
                        input = null;

                    final String finalInput = input;
                    if(validationFunction.apply(finalInput)) {
                        return List.of(SignGUIAction.displayNewLines(lines));
                    } else {
                        return List.of(SignGUIAction.run(() -> Elevators.getFoliaLib().getScheduler().runNextTick((task) -> resultConsumer.accept(finalInput))));
                    }
                });

                player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
                builder.build().open(player);
                return;
            } catch (SignGUIVersionException ex) {
                signEnabled = false;
            }
        }

        player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        if(allowReset)
            input.allowReset();

        input.onComplete(result -> {
            if(validationFunction.apply(result)) {
                resultConsumer.accept(result);
                return SimpleInput.SimpleInputResult.STOP;
            }
            return SimpleInput.SimpleInputResult.CONTINUE;
        });
        input.onCancel(onCancel);
        MessageHelper.sendFormattedMessage(player, inputMessage);
        input.start();
    }

    public static void tryOpenAnvil(Player player, Function<String, Boolean> validationFunction, Consumer<String> resultConsumer, Runnable onCancel, String inputMessage, boolean allowReset, String defaultText, String title) {
        if(anvilEnabled){
            Function<String, String> cleanText = result -> {
                if (allowReset && (result.isEmpty() || result.equalsIgnoreCase("reset")))
                    result = null;

                return result;
            };

            AnvilGUI.Builder anvilBuilder = new AnvilGUI.Builder();
            anvilBuilder.preventClose();
            anvilBuilder.text(defaultText);
            anvilBuilder.title(title);
            anvilBuilder.plugin(Elevators.getInstance());
            anvilBuilder.onClose(state -> resultConsumer.accept(cleanText.apply(state.getText())));
            anvilBuilder.onClick((slot, state) -> {
                if (slot != AnvilGUI.Slot.OUTPUT)
                    return Collections.emptyList();

                String result = cleanText.apply(state.getText());
                if(validationFunction.apply(result))
                    return List.of(AnvilGUI.ResponseAction.close());

                return List.of(AnvilGUI.ResponseAction.replaceInputText(defaultText));
            });

            player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
            anvilBuilder.open(player);
            return;
        }

        player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        if(allowReset)
            input.allowReset();

        input.onComplete(result -> {
            if(validationFunction.apply(result)) {
                resultConsumer.accept(result);
                return SimpleInput.SimpleInputResult.STOP;
            }
            return SimpleInput.SimpleInputResult.CONTINUE;
        });
        input.onCancel(onCancel);
        MessageHelper.sendFormattedMessage(player, inputMessage);
        input.start();
    }

    public static void openConfirmMenu(Player player, Consumer<Boolean> onConfirm) {

        Inventory inventory = Bukkit.createInventory(null, 54, "Are you sure?");

        BiConsumer<InventoryClickEvent, SimpleDisplay> confirmConsumer = (event, myDisplay) -> {
            if(event.getCurrentItem() == null)
                return;
            myDisplay.stopReturn();
            onConfirm.accept(event.getCurrentItem().getType() == Material.LIME_WOOL);
        };

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> onConfirm.accept(false));
        for (int i = 0; i < 54; i++)
            display.setItemSimple(i, (i % 9) < 4 ? ItemStackHelper.createItem("Accept", Material.LIME_WOOL, 1) : ((i % 9) > 4 ? ItemStackHelper.createItem("Deny", Material.RED_WOOL, 1) : null), confirmConsumer);

        display.open();
    }

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

    public static void openAdminActionSettingsMenu(Player player, ElevatorType elevatorType, ElevatorAction action, Runnable onReturn) {

        List<ElevatorActionSetting<?>> settings = new ArrayList<>(action.getSettings());

        int inventorySize = (Math.floorDiv(settings.size() + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Settings > Actions > Action");

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory);
        action.onStartEditing(player, display, null);
        display.onReturn(() -> {
            action.onStopEditing(player, display, null);
            onReturn.run();
        });

        for (int i = 0; i < settings.size(); i++) {
            ElevatorActionSetting<?> setting = settings.get(i);
            display.setItemSimple(i + 9, setting.createIcon(setting.getCurrentValueGlobal(elevatorType), true), (event, myDisplay) -> {
                myDisplay.stopReturn();
                setting.clickGlobal(player, elevatorType, () -> openAdminActionSettingsMenu(player, elevatorType, action, onReturn), event);
            });
        }
        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));
        display.open();
    }

    public static void openAdminActionsMenu(Player player, ElevatorType elevatorType, List<ElevatorAction> actions) {

        int inventorySize = (Math.floorDiv(actions.size() + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Admin > Settings > Actions");

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> openAdminSettingsMenu(player, elevatorType));
        for (int i = 0; i < actions.size(); i++) {
            ElevatorAction action = actions.get(i);
            display.setItemSimple(i + 9, action.getIcon(), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openAdminActionSettingsMenu(player, elevatorType, action, () -> openAdminActionsMenu(player, elevatorType, actions));
            });
        }
        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));

        //TODO: Add an "Add Action" button

        display.open();
    }

    public static void openAdminSettingsMenu(Player player, ElevatorType elevatorType) {
        List<ElevatorSetting<?>> settings = ElevatorSettingService.getElevatorSettings();

        int itemAmount = settings.size() + 2;
        List<ElevatorAction> upActions = elevatorType.getActionsUp();
        List<ElevatorAction> downActions = elevatorType.getActionsDown();

        int inventorySize = (Math.floorDiv(itemAmount + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Admin > Settings");

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> openAdminMenu(player));
        for (int i = 0; i < settings.size(); i++) {
            ElevatorSetting<?> setting = settings.get(i);
            display.setItemSimple(i + 9, setting.createIcon(setting.getCurrentValueGlobal(elevatorType), true), (event, myDisplay) -> {
                myDisplay.stopReturn();
                setting.clickGlobal(player, elevatorType, () -> openAdminSettingsMenu(player, elevatorType), event);
            });
        }

        if (!downActions.isEmpty()) {
            display.setItemSimple(inventory.getSize() - 1, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Downwards Actions", Material.SPECTRAL_ARROW, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openAdminActionsMenu(player, elevatorType, downActions);
            });
        }

        if (!upActions.isEmpty()) {
            display.setItemSimple(inventory.getSize() - (downActions.isEmpty() ? 1 : 2), ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Upwards Actions", Material.TIPPED_ARROW, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openAdminActionsMenu(player, elevatorType, upActions);
            });
        }

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));

        display.open();
    }

    public static void openDeleteElevatorTypeMenu(Player player, ElevatorType elevatorType) {
        openConfirmMenu(player, confirmed -> {
            if(confirmed)
                ElevatorTypeService.unregisterElevatorType(elevatorType);

            openAdminMenu(player);
        });
    }

    public static void openCreateElevatorTypeMenu(Player player) {
        player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(result -> {
            if(result == null) {
                openAdminMenu(player);
                return SimpleInput.SimpleInputResult.STOP;
            }

            if(ElevatorTypeService.getElevatorType(result) != null) {
                MessageHelper.sendFormattedMessage(player, ConfigService.getRootConfig().locale.nonUniqueElevatorKey);
                return SimpleInput.SimpleInputResult.CONTINUE;
            }

            openAdminSettingsMenu(player, ElevatorTypeService.createElevatorType(result));
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(() -> openAdminMenu(player));
        MessageHelper.sendFormattedMessage(player, ConfigService.getRootConfig().locale.enterMessage);
        input.start();
    }

    public static void openAdminMenu(Player player) {

        PagedDisplay<ElevatorType> display = new PagedDisplay<>(Elevators.getInstance(), player, ElevatorTypeService.getExistingElevatorTypes(), "Admin");
        display.onCreateItem(elevatorType -> {

            DyeColor color = DyeColor.getByWoolData((byte) (Math.abs(elevatorType.getTypeKey().hashCode()) % 16));
            if (elevatorType.canElevatorBeDyed() && !elevatorType.getRecipeGroups().isEmpty())
                color = elevatorType.getRecipeGroups().getFirst().getDefaultOutputColor();

            ItemStack icon = ItemStackHelper.createItemStackFromElevatorType(elevatorType, color);
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Type Key: " + elevatorType.getTypeKey());
            meta.setLore(lore);
            icon.setItemMeta(meta);

            return icon;
        });
        display.onClick((item, event, myDisplay) -> {
            myDisplay.stopReturn();
            if (event.isShiftClick())
                openDeleteElevatorTypeMenu(player, item);
            else
                openAdminSettingsMenu(player, item);
        });

        display.onLoad((tempDisplay, page) -> {
            int addElevatorIndex = display.getDisplay().getInventory().getSize() - 1;
            display.getDisplay().setItemSimple(addElevatorIndex, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Add Elevator", Material.NETHER_STAR, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openCreateElevatorTypeMenu(player);
            });
        });

        display.open();

    }

    public static void openInteractMenu(Player player, Elevator elevator) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Elevator");

        ElevatorHelper.setElevatorDisabled(elevator.getShulkerBox());
        ShulkerBoxHelper.playOpen(elevator.getShulkerBox());

        ElevatorGUIHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

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

            if (canRename) {
                display.setItemSimple(11, nameItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, ElevatorGUIHelper::openInteractNameMenu));
                display.setItemSimple(15, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, ElevatorGUIHelper::openInteractSettingsMenu));
            } else {
                display.setItemSimple(13, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, ElevatorGUIHelper::openInteractSettingsMenu));
            }
            display.open();
            return;
        }

        if (canRename) {
            display.setItemSimple(10, protectionItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, ElevatorGUIHelper::openInteractProtectMenu));
            display.setItemSimple(13, nameItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, ElevatorGUIHelper::openInteractNameMenu));
            display.setItemSimple(16, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, ElevatorGUIHelper::openInteractSettingsMenu));
        } else {
            display.setItemSimple(11, protectionItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, ElevatorGUIHelper::openInteractProtectMenu));
            display.setItemSimple(15, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, ElevatorGUIHelper::openInteractSettingsMenu));
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

        Inventory inventory = ElevatorGUIHelper.createInventoryWithMinSlots(protectionHooks.size() + 9, "Elevator > Protection");
        ElevatorGUIHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> openInteractMenu(player, elevator));
        for (Map.Entry<ProtectionHook, Integer> hookData : ElevatorGUIHelper.mapToInventorySlot(protectionHooks).entrySet()) {
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

        tryOpenSign(player, value -> true, result -> {
            DataContainerService.setFloorName(elevator, result);
            ElevatorGUIHelper.openInteractMenu(player, elevator);
        },() -> {
            ElevatorGUIHelper.openInteractMenu(player, elevator);
        },ConfigService.getRootConfig().locale.enterFloorName, true, currentName, ChatColor.BOLD + "^^^^^^^^", "Enter floor", "name above");
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
        ElevatorGUIHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory);
        action.onStartEditing(player, display, elevator);
        display.onReturn(() -> {
            action.onStopEditing(player, display, elevator);
            onReturn.run();
        });

        for (int i = 0; i < settings.size(); i++) {
            ElevatorActionSetting<?> setting = settings.get(i);
            display.setItemSimple(i + 9, setting.createIcon(setting.getIndividualElevatorValue(elevator), false), (event, myDisplay) -> {
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
        ElevatorGUIHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> openInteractSettingsMenu(player, elevator));
        for (int i = 0; i < actions.size(); i++) {
            ElevatorAction action = actions.get(i);
            display.setItemSimple(i + 9, action.getIcon(), (event, myDisplay) -> {
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

        if (!upActions.isEmpty())
            itemAmount++;
        if (!downActions.isEmpty())
            itemAmount++;

        int inventorySize = (Math.floorDiv(itemAmount + 8, 9) * 9) + 9;
        Inventory inventory = Bukkit.createInventory(null, inventorySize, "Elevator > Settings");
        ElevatorGUIHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> openInteractMenu(player, elevator));
        for (int i = 0; i < settings.size(); i++) {
            ElevatorSetting<?> setting = settings.get(i);
            display.setItemSimple(i + 9, setting.createIcon(setting.getIndividualElevatorValue(elevator), false), (event, myDisplay) -> {
                myDisplay.stopReturn();
                setting.clickIndividual(player, elevator, () -> openInteractSettingsMenu(player, elevator), event);
            });
        }

        if (!downActions.isEmpty()) {
            display.setItemSimple(inventory.getSize() - 1, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Downwards Actions", Material.SPECTRAL_ARROW, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openInteractActionsMenu(player, elevator, downActions);
            });
        }

        if (!upActions.isEmpty()) {
            display.setItemSimple(inventory.getSize() - (downActions.isEmpty() ? 1 : 2), ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Upwards Actions", Material.TIPPED_ARROW, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openInteractActionsMenu(player, elevator, upActions);
            });
        }

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));

        display.open();
    }

}
