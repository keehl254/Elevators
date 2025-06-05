package me.keehl.elevators.helpers;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.models.*;
import me.keehl.elevators.models.settings.ElevatorSetting;
import me.keehl.elevators.models.hooks.ProtectionHook;
import me.keehl.elevators.services.*;
import me.keehl.elevators.services.configs.versions.configv5.ConfigRecipe;
import me.keehl.elevators.services.interaction.PagedDisplay;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import me.keehl.elevators.services.interaction.SimpleInput;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.SignGUIBuilder;
import de.rapha149.signgui.exception.SignGUIVersionException;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.version.VersionMatcher;
import net.wesjd.anvilgui.version.VersionWrapper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public class ElevatorGUIHelper {

    private static boolean anvilEnabled;
    private static boolean signEnabled;

    static {

        // AnvilGUI and SignGUI do not support Folia.
        if (!Elevators.getFoliaLib().isFolia()) {
            try {
                SignGUI.builder();
                signEnabled = true;
            } catch (SignGUIVersionException e) {
                signEnabled = false;
            }

            anvilEnabled = true;
            try {
                VersionWrapper wrapper = (new VersionMatcher()).match();
                Method toNMSMethod = wrapper.getClass().getDeclaredMethod("sendPacketCloseWindow", Player.class, int.class);
                toNMSMethod.setAccessible(true);

                try {
                    toNMSMethod.invoke(wrapper, null, -1);
                } catch (InvocationTargetException ite) {
                    if (ite.getTargetException() instanceof NoClassDefFoundError) {
                        anvilEnabled = false;
                        Elevators.getElevatorsLogger().warning("AnvilAPI is not up-to-date. Using backup chat input system.");
                    } else if (!(ite.getTargetException() instanceof NullPointerException)) {// NPE will occur if the AnvilGUI is up-to-date.
                        Elevators.getElevatorsLogger().warning(ite.getTargetException().toString());
                        anvilEnabled = false;
                    }
                }

            } catch (Exception ex) {
                Elevators.getElevatorsLogger().warning("AnvilAPI is not up-to-date. Using backup chat input system.");
                Elevators.getElevatorsLogger().warning(ex.toString());
                anvilEnabled = false;
            }
        }

    }

    public static void tryOpenSign(Player player, Function<String, Boolean> validationFunction, Consumer<String> resultConsumer, Runnable onCancel, String inputMessage, boolean allowReset, String... lines) {
        if (signEnabled) {
            try {
                SignGUIBuilder builder = SignGUI.builder();
                builder.setLines(lines);
                builder.setHandler((p, result) -> {
                    String input = result.getLineWithoutColor(0).trim();
                    if (allowReset && (input.isEmpty() || input.equalsIgnoreCase("reset")))
                        input = null;

                    final String finalInput = input;
                    if (validationFunction.apply(finalInput)) {
                        return Collections.singletonList(SignGUIAction.runSync(Elevators.getInstance(), () -> resultConsumer.accept(finalInput)));
                    } else {
                        return Collections.singletonList(SignGUIAction.displayNewLines(lines));
                    }
                });

                player.closeInventory();
                builder.build().open(player);
                return;
            } catch (SignGUIVersionException ex) {
                signEnabled = false;
            }
        }

        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        if (allowReset)
            input.allowReset();

        input.onComplete(result -> {
            if (validationFunction.apply(result)) {
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

        // Anvil GUI is cool; however, there is no way to tell if the API supports the current game version.
        // I will put this back when they fix this.

        if (anvilEnabled) {
            try {
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
                    if (validationFunction.apply(result))
                        return Collections.singletonList(AnvilGUI.ResponseAction.close());

                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText(defaultText));
                });

                player.closeInventory();
                anvilBuilder.open(player);
                return;
            } catch (Exception ex) {
                anvilEnabled = false;
            }
        }

        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        if (allowReset)
            input.allowReset();

        input.onComplete(result -> {
            if (validationFunction.apply(result)) {
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
            if (event.getCurrentItem() == null)
                return;
            myDisplay.stopReturn();
            onConfirm.accept(event.getCurrentItem().getType() == Material.LIME_WOOL);
        };

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> onConfirm.accept(false));
        for (int i = 0; i < 54; i++)
            display.setItemSimple(i, (i % 9) < 4 ? ItemStackHelper.createItem("Accept", Material.LIME_WOOL, 1) : ((i % 9) > 4 ? ItemStackHelper.createItem("Deny", Material.RED_WOOL, 1) : null), confirmConsumer);

        display.open();
    }

    public static void openChooseDyeColorMenu(Player player, String title, Consumer<DyeColor> returnMethod, Runnable onCancel) {
        PagedDisplay<DyeColor> display = new PagedDisplay<>(Elevators.getInstance(), player, Arrays.asList(DyeColor.values()), title, onCancel);
        display.onCreateItem(dyeColor ->
                ItemStackHelper.createItem(ColorHelper.getChatStringFromColor(dyeColor.getColor().asRGB()) + ChatColor.BOLD + dyeColor.name(), ItemStackHelper.getVariant(Material.BLACK_DYE, dyeColor), 1)
        );
        display.onClick((dyeColor, event, myDisplay) -> {
            display.stopReturn();
            returnMethod.accept(dyeColor);
        });
        display.open();
    }

    private static Map<String, String> createActionMap(List<String> keyList, List<String> actionList) {
        Map<String, String> actions = new HashMap<>();
        for(int i=0;i<keyList.size();i++) {
            String key = keyList.get(i);
            if(actionList.size() > i)
                actions.put(key, actionList.get(i));
        }

        return actions;
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

    public static <T> ItemStack createValueButton(ItemStack template, T value, Function<T, String> serializeMethod, Map<String, String> actions) {
        List<String> lore = new ArrayList<>();

        ItemMeta templateMeta = template.getItemMeta();
        if (templateMeta.hasLore())
            lore.addAll(Objects.requireNonNull(templateMeta.getLore()));

        lore.add("");
        lore.add(ChatColor.GRAY + "Current Value: ");
        if (value instanceof Boolean) {
            Boolean boolValue = (Boolean) value;
            lore.add(boolValue ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED"));
        } else
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + serializeMethod.apply(value));

        if (!actions.isEmpty()) {
            lore.add("");
            actions.forEach((action, description) -> lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + action + ": " + ChatColor.GRAY + description));
        }

        ItemStack icon = template.clone();
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setLore(lore);
        icon.setItemMeta(iconMeta);

        return icon;
    }

    public static ItemStack createBooleanButton(ItemStack template, boolean value, Map<String, String> actions) {
        return createValueButton(template, value, boolValue -> boolValue ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED"), actions);
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

    public static void openAdminActionSettingsMenu(Player player, ElevatorType tempElevatorType, ElevatorAction action, Runnable onReturn) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

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

    public static void openAdminCreateActionMenu(Player player, ElevatorType tempElevatorType, List<ElevatorAction> currentActionList) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        List<String> registeredActions = ElevatorActionService.getRegisteredActions();

        PagedDisplay<String> display = new PagedDisplay<>(Elevators.getInstance(), player, registeredActions, "Settings > Actions > Create", () -> openAdminSettingsMenu(player, elevatorType));
        display.onCreateItem(ElevatorActionService::getActionIcon);
        display.onClick((item, event, myDisplay) -> {
            myDisplay.stopReturn();
            ElevatorAction newAction = ElevatorActionService.createBlankAction(elevatorType, item);
            currentActionList.add(newAction);
            openAdminActionSettingsMenu(player, elevatorType, newAction, () -> openAdminActionsMenu(player, elevatorType, currentActionList));
        });

        display.open();

    }

    public static void openAdminActionsMenu(Player player, ElevatorType tempElevatorType, List<ElevatorAction> actions) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        PagedDisplay<ElevatorAction> display = new PagedDisplay<>(Elevators.getInstance(), player, actions, "Admin > Settings > Actions", () -> openAdminSettingsMenu(player, elevatorType));
        display.onCreateItem(action -> {
            ItemStack template = action.getIcon().clone();
            ItemMeta meta = template.getItemMeta();
            assert meta != null;

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "Edit Action");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Shift Click: " + ChatColor.GRAY + "Delete Action");

            meta.setLore(lore);
            template.setItemMeta(meta);
            return template;
        });
        display.onClick((item, event, myDisplay) -> {
            myDisplay.stopReturn();

            if(event.isShiftClick()) {
                openConfirmMenu(player, confirm -> {
                    if(confirm)
                        actions.remove(item);

                    openAdminActionsMenu(player, elevatorType, actions);
                });
                return;
            }
            openAdminActionSettingsMenu(player, elevatorType, item, () -> openAdminActionsMenu(player, elevatorType, actions));
        });
        display.onLoad((tempDisplay, page) -> {
            int addRecipeIndex = display.getDisplay().getInventory().getSize() - 1;
            display.getDisplay().setItemSimple(addRecipeIndex, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Add Action", Material.NETHER_STAR, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openAdminCreateActionMenu(player, elevatorType, actions);
            });
        });

        display.open();
    }

    public static void openSaveRecipeMenu(Player player, ElevatorType tempElevatorType, ElevatorRecipeGroup recipeGroup) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }
        Runnable onReturn = () -> openAdminEditRecipesMenu(player, elevatorType);
        if (recipeGroup.getRecipeKey() != null) {
            elevatorType.getRecipeMap().put(recipeGroup.getRecipeKey(), recipeGroup);
            onReturn.run();
            ElevatorRecipeService.refreshRecipes();
            return;
        }

        ElevatorGUIHelper.tryOpenAnvil(player, value -> {
            if (elevatorType.getRecipeMap().containsKey(value.toUpperCase())) {
                MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.nonUniqueRecipeName);
                return false;
            }
            return true;
        }, result -> {
            recipeGroup.setKey(result.toUpperCase());
            elevatorType.getRecipeMap().put(result.toUpperCase(), recipeGroup);
            onReturn.run();
            ElevatorRecipeService.refreshRecipes();
        }, onReturn, ElevatorConfigService.getRootConfig().locale.enterRecipeName, false, "", "Enter recipe name.");

    }

    public static void openEditRecipePermissionMenu(Player player, ElevatorType tempElevatorType, ElevatorRecipeGroup recipeGroup) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.allowReset();

        input.onComplete(result -> {

            if (result == null)
                result = "elevators.craft." + elevatorType.getTypeKey();

            ConfigRecipe.setCraftPermission(recipeGroup, result);
            openAdminEditElevatorRecipeMenu(player, elevatorType, recipeGroup);
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(() -> openAdminEditElevatorRecipeMenu(player, elevatorType, recipeGroup));
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterRecipePermission);
        input.start();

    }

    // Elevators most complicated menu.
    public static void openAdminEditElevatorRecipeMenu(Player player, ElevatorType tempElevatorType, ElevatorRecipeGroup currentRecipeGroup) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 54, "Settings > Recipes > Recipe");

        ElevatorRecipeGroup tempRecipe = new ElevatorRecipeGroup();
        if (currentRecipeGroup != null) {
            ConfigRecipe.setAmount(tempRecipe, currentRecipeGroup.amount);
            ConfigRecipe.setCraftPermission(tempRecipe, currentRecipeGroup.getCraftPermission());
            ConfigRecipe.setDefaultOutputColor(tempRecipe, currentRecipeGroup.getDefaultOutputColor());
            ConfigRecipe.setMultiColorOutput(tempRecipe, currentRecipeGroup.supportMultiColorOutput());
            ConfigRecipe.setMultiColorMaterials(tempRecipe, currentRecipeGroup.supportMultiColorMaterials());
            ConfigRecipe.setRecipe(tempRecipe, currentRecipeGroup.getRecipe());
            ConfigRecipe.setMaterials(tempRecipe, currentRecipeGroup.getMaterialMap());
            tempRecipe.setKey(currentRecipeGroup.getRecipeKey());
        }

        Runnable saveToTemp = () -> {
            Map<Material, Character> materialCharacterMap = new HashMap<>();
            Map<Character, Material> newMaterialMap = new HashMap<>();
            String[] recipe = new String[]{"","",""};

            ItemStack[] items = new ItemStack[]{
                    inventory.getItem(10),
                    inventory.getItem(11),
                    inventory.getItem(12),
                    inventory.getItem(19),
                    inventory.getItem(20),
                    inventory.getItem(21),
                    inventory.getItem(28),
                    inventory.getItem(29),
                    inventory.getItem(30)
            };

            char newChar = 'A'; // Can only really go up to I, so not a problem doing it this way.
            for (int i = 0; i < 9; i++) {

                ItemStack item = items[i];
                char character = ' ';
                if (item != null) {

                    // Prefer the starting char of the item if possible.
                    char chosenChar = item.getType().getKey().getKey().toLowerCase().charAt(0);
                    if (!materialCharacterMap.containsKey(item.getType())) {
                        if(newMaterialMap.containsKey(chosenChar))
                            chosenChar = newChar++;

                        materialCharacterMap.put(item.getType(), chosenChar);
                    }

                    character = materialCharacterMap.get(item.getType());
                    newMaterialMap.put(character, item.getType());
                }

                int recipeRow = i / 3;
                recipe[recipeRow] += character;
            }

            ConfigRecipe.setMaterials(tempRecipe, newMaterialMap);
            ConfigRecipe.setRecipe(tempRecipe, Arrays.asList(recipe));
        };

        AtomicInteger dyeColorIndex = new AtomicInteger(0);
        WrappedTask colorTask = Elevators.getFoliaLib().getScheduler().runTimer(() -> {
            DyeColor color = null;
            if(!ConfigRecipe.supportsMultiColorOutput(tempRecipe)) {
                color = ConfigRecipe.getDefaultOutputColor(tempRecipe);
            } else if(!ConfigRecipe.supportsMultiColorMaterials(tempRecipe)) {
                for(String line : ConfigRecipe.getRecipe(tempRecipe)) {
                    for(char character : line.toCharArray()) {
                        DyeColor tempColor = ItemStackHelper.getDyeColorFromMaterial(ConfigRecipe.getMaterials(tempRecipe).getOrDefault(character, Material.AIR));
                        if(tempColor == null)
                            continue;

                        if(color != null && tempColor != color) {
                            color = ConfigRecipe.getDefaultOutputColor(tempRecipe); // Multiple colorable materials in recipe. Cannot determine output color.
                            break;
                        }
                        color = tempColor;
                    }
                }
            } else
                color = DyeColor.values()[dyeColorIndex.get()];

            ItemStack elevatorItemStack = ItemStackHelper.createItemStackFromElevatorType(elevatorType, color);
            elevatorItemStack.setAmount(ConfigRecipe.getAmount(tempRecipe));

            inventory.setItem(25, elevatorItemStack);
            dyeColorIndex.set(dyeColorIndex.incrementAndGet() % DyeColor.values().length);
        }, 20, 20);

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> {
            colorTask.cancel();
            openAdminEditRecipesMenu(player, elevatorType);
        }, SimpleDisplay.DisplayClickResult.CANCEL, SimpleDisplay.DisplayClickResult.ALLOW);
        fillEmptySlotsWithPanes(inventory, DyeColor.BLACK);

        Material[] materials = new Material[9];

        for (int i = 0; i < 3; i++) {
            if (ConfigRecipe.getRecipe(tempRecipe).size() <= i)
                continue;

            String line = ConfigRecipe.getRecipe(tempRecipe).get(i);
            materials[(i * 3)] = ConfigRecipe.getMaterials(tempRecipe).getOrDefault((!line.isEmpty() ? line.charAt(0) : ' '), null);
            materials[(i * 3) + 1] = ConfigRecipe.getMaterials(tempRecipe).getOrDefault((line.length() > 1 ? line.charAt(1) : ' '), null);
            materials[(i * 3) + 2] = ConfigRecipe.getMaterials(tempRecipe).getOrDefault((line.length() > 2 ? line.charAt(2) : ' '), null);
        }

        for (int i = 0; i < materials.length; i++) {
            Material type = materials[i];

            int col = i % 3;
            int row = i / 3;

            int slot = 10 + (row * 9) + col;

            ItemStack item = new ItemStack(type == null ? Material.AIR : type);
            BiFunction<InventoryClickEvent, SimpleDisplay, SimpleDisplay.DisplayClickResult> onClick = (event, myDisplay) -> SimpleDisplay.DisplayClickResult.ALLOW;

            display.setItem(slot, item, onClick);
        }

        ItemStack permissionTemplate = ItemStackHelper.createItem(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Craft Permission", Material.CHAIN_COMMAND_BLOCK, 1, Arrays.asList("",
                ChatColor.GRAY + "Current Value: ",
                ChatColor.GOLD + ConfigRecipe.getCraftPermission(tempRecipe))
        );
        ItemStack outputColorTemplate = ItemStackHelper.createItem(
                ColorHelper.getChatStringFromColor(ConfigRecipe.getDefaultOutputColor(tempRecipe).getColor().asRGB()) + ChatColor.BOLD + "Change Default Output Color",
                ItemStackHelper.getVariant(Material.BLACK_DYE, ConfigRecipe.getDefaultOutputColor(tempRecipe)),
                1
        );
        ItemStack multiColorMaterialTemplate = ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Multi-Color Materials", Material.FIREWORK_STAR, 1);
        ItemStack multiColorOutputTemplate = ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "Multi-Color Output", Material.FIREWORK_ROCKET, 1);

        ItemStack amountTemplate = ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "Quantity", Material.EXPERIENCE_BOTTLE, 1);
        Function<Integer, ItemStack> createAmountIcon = amount -> createValueButton(amountTemplate, amount, Object::toString, createActionMap(Arrays.asList("Left Click", "Right Click"), Arrays.asList("Increase Quantity", "Decrease Quantity")));
        Supplier<ItemStack> createMultiColorMaterialIcon = () -> createBooleanButton(multiColorMaterialTemplate, ConfigRecipe.supportsMultiColorMaterials(tempRecipe), createActionMap(Collections.singletonList("Left Click"), Collections.singletonList("Toggle")));
        Supplier<ItemStack> createMultiColorOutputIcon = () -> createBooleanButton(multiColorOutputTemplate, ConfigRecipe.supportsMultiColorOutput(tempRecipe), createActionMap(Collections.singletonList("Left Click"), Collections.singletonList("Toggle")));

        display.setReturnButton(45, ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "CANCEL", Material.BARRIER, 1));
        display.setItemSimple(47, permissionTemplate, (event, myDisplay) -> {
            saveToTemp.run();
            colorTask.cancel();
            display.stopReturn();
            openEditRecipePermissionMenu(player, elevatorType, tempRecipe);
        });

        display.setItemSimple(48, createAmountIcon.apply(ConfigRecipe.getAmount(tempRecipe)), (event, myDisplay) -> {
            int newValue = ConfigRecipe.getAmount(tempRecipe) + (event.isLeftClick() ? 1 : -1);
            newValue = Math.min(Math.max(newValue, 1), elevatorType.getMaxStackSize());
            ConfigRecipe.setAmount(tempRecipe, newValue);
            inventory.setItem(48, createAmountIcon.apply(newValue));
        });

        display.setItemSimple(49, createValueButton(outputColorTemplate, ConfigRecipe.getDefaultOutputColor(tempRecipe), DyeColor::name, createActionMap(Collections.singletonList("Left Click"), Collections.singletonList("Change Color"))), (event, myDisplay) -> {
            saveToTemp.run();
            colorTask.cancel();
            display.stopReturn();
            openChooseDyeColorMenu(player, "Recipes > Recipe > Color", color -> {
                ConfigRecipe.setDefaultOutputColor(tempRecipe, color);

                openAdminEditElevatorRecipeMenu(player, elevatorType, tempRecipe);
            }, () -> openAdminEditElevatorRecipeMenu(player, elevatorType, tempRecipe));
        });

        display.setItemSimple(50, createMultiColorMaterialIcon.get(), (event, myDisplay) -> {
            ConfigRecipe.setMultiColorMaterials(tempRecipe, !ConfigRecipe.supportsMultiColorMaterials(tempRecipe));
            inventory.setItem(50, createMultiColorMaterialIcon.get());
        });

        display.setItemSimple(51, createMultiColorOutputIcon.get(), (event, myDisplay) -> {
            ConfigRecipe.setMultiColorOutput(tempRecipe, !ConfigRecipe.supportsMultiColorOutput(tempRecipe));
            inventory.setItem(51, createMultiColorOutputIcon.get());
        });

        display.setItemSimple(53, ItemStackHelper.createItem(ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE", Material.ARROW, 1), (event, myDisplay) -> {
            saveToTemp.run();
            colorTask.cancel();
            display.stopReturn();
            openSaveRecipeMenu(player, elevatorType, tempRecipe);
        });

        display.open();
    }

    public static void openAdminDeleteElevatorRecipe(Player player, ElevatorType tempElevatorType, ElevatorRecipeGroup recipeGroup) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        openConfirmMenu(player, confirmed -> {
            if (confirmed) {
                elevatorType.getRecipeMap().remove(recipeGroup.getRecipeKey());
                ElevatorRecipeService.refreshRecipes();
            }

            openAdminEditRecipesMenu(player, elevatorType);
        });
    }

    public static void openAdminEditRecipesMenu(Player player, ElevatorType tempElevatorType) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        PagedDisplay<ElevatorRecipeGroup> display = new PagedDisplay<>(Elevators.getInstance(), player, elevatorType.getRecipeGroups(), "Admin > Settings > Recipes", () -> openAdminSettingsMenu(player, elevatorType));
        display.onCreateItem(recipeGroup -> {

            DyeColor color = DyeColor.getByWoolData((byte) (Math.abs(recipeGroup.getRecipeKey().hashCode()) % 16));
            if (!recipeGroup.supportMultiColorOutput() || color == null)
                color = recipeGroup.getDefaultOutputColor();

            String chatColor = ColorHelper.getChatStringFromColor(color.getColor().asRGB());

            ItemStack icon = ItemStackHelper.createItem(chatColor + ChatColor.BOLD + recipeGroup.getRecipeKey(), ItemStackHelper.getVariant(Material.RED_SHULKER_BOX, color), recipeGroup.amount);
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "Edit Recipe");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Shift Click: " + ChatColor.GRAY + "Delete Recipe");
            meta.setLore(lore);
            icon.setItemMeta(meta);

            return icon;
        });
        display.onClick((item, event, myDisplay) -> {
            myDisplay.stopReturn();
            if (event.isShiftClick())
                openAdminDeleteElevatorRecipe(player, elevatorType, item);
            else
                openAdminEditElevatorRecipeMenu(player, elevatorType, item);
        });

        display.onLoad((tempDisplay, page) -> {
            int addRecipeIndex = display.getDisplay().getInventory().getSize() - 1;
            display.getDisplay().setItemSimple(addRecipeIndex, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Add Recipe", Material.NETHER_STAR, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                openAdminEditElevatorRecipeMenu(player, elevatorType, null);
            });
        });

        display.open();
    }

    public static void openAdminSettingsMenu(Player player, ElevatorType tempElevatorType) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        List<ElevatorSetting<?>> settings = ElevatorSettingService.getElevatorSettings();

        int itemAmount = settings.size() + 3;
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

        display.setItemSimple(inventory.getSize() - 3, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Recipes", Material.MAP, 1), (event, myDisplay) -> {
            myDisplay.stopReturn();
            openAdminEditRecipesMenu(player, elevatorType);
        });

        display.setItemSimple(inventory.getSize() - 1, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Downwards Actions", Material.SPECTRAL_ARROW, 1), (event, myDisplay) -> {
            myDisplay.stopReturn();
            openAdminActionsMenu(player, elevatorType, downActions);
        });

        display.setItemSimple(inventory.getSize() - 2, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Upwards Actions", Material.TIPPED_ARROW, 1), (event, myDisplay) -> {
            myDisplay.stopReturn();
            openAdminActionsMenu(player, elevatorType, upActions);
        });

        display.setReturnButton(0, ItemStackHelper.createItem(ChatColor.GRAY + "" + ChatColor.BOLD + "BACK", Material.ARROW, 1));

        display.open();
    }

    public static void openAdminDeleteElevatorTypeMenu(Player player, ElevatorType tempElevatorType) {
        final ElevatorType elevatorType = ElevatorTypeService.getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        openConfirmMenu(player, confirmed -> {
            if (confirmed)
                ElevatorTypeService.unregisterElevatorType(elevatorType);

            openAdminMenu(player);
        });
    }

    public static void openCreateElevatorTypeMenu(Player player) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(result -> {
            if (result == null) {
                openAdminMenu(player);
                return SimpleInput.SimpleInputResult.STOP;
            }

            if (ElevatorTypeService.getElevatorType(result) != null) {
                MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.nonUniqueElevatorKey);
                return SimpleInput.SimpleInputResult.CONTINUE;
            }

            openAdminSettingsMenu(player, ElevatorTypeService.createElevatorType(result));
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(() -> openAdminMenu(player));
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterMessage);
        input.start();
    }

    public static void openAdminMenu(Player player) {

        PagedDisplay<ElevatorType> display = new PagedDisplay<>(Elevators.getInstance(), player, ElevatorTypeService.getExistingElevatorTypes(), "Admin");
        display.onCreateItem(elevatorType -> {

            DyeColor color = DyeColor.getByWoolData((byte) (Math.abs(elevatorType.getTypeKey().hashCode()) % 16));
            if (elevatorType.canElevatorBeDyed() && !elevatorType.getRecipeGroups().isEmpty())
                color = elevatorType.getRecipeGroups().get(0).getDefaultOutputColor();

            ItemStack icon = ItemStackHelper.createItemStackFromElevatorType(elevatorType, color);
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Type Key: " + elevatorType.getTypeKey());
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "Edit Elevator");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Shift Click: " + ChatColor.GRAY + "Delete Elevator");
            meta.setLore(lore);
            icon.setItemMeta(meta);

            return icon;
        });
        display.onClick((item, event, myDisplay) -> {
            myDisplay.stopReturn();
            if (event.isShiftClick())
                openAdminDeleteElevatorTypeMenu(player, item);
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
        if (!elevator.isValid()) {
            MessageHelper.sendElevatorChangedMessage(player, new ElevatorEventData(elevator, elevator, (byte) 1, 0));
            if (elevator.getShulkerBox() != null && ElevatorHelper.isElevatorDisabled(elevator.getShulkerBox())) {
                ElevatorHelper.setElevatorEnabled(elevator.getShulkerBox());
                ShulkerBoxHelper.playClose(elevator.getShulkerBox());
            }

            player.closeInventory();
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 27, "Elevator");

        ElevatorHelper.setElevatorDisabled(elevator.getShulkerBox());
        ShulkerBoxHelper.playOpen(elevator.getShulkerBox());

        ElevatorGUIHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        List<String> nameLore = new ArrayList<>();
        nameLore.add("");
        nameLore.add(ChatColor.GRAY + "Current Value: ");
        nameLore.add(ChatColor.GOLD + "" + ChatColor.BOLD + ElevatorDataContainerService.getFloorName(elevator));

        ItemStack protectionItem = ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "Protection", Material.DIAMOND_SWORD, 1);
        ItemStack nameItem = ItemStackHelper.createItem(ChatColor.YELLOW + "" + ChatColor.BOLD + "Floor Name", Material.NAME_TAG, 1, nameLore);
        ItemStack settingsItem = ItemStackHelper.createItem(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Settings", Material.SEA_LANTERN, 1);

        List<ProtectionHook> protectionHooks = ElevatorHookService.getProtectionHooks().stream().filter(i -> i.getConfig().allowCustomization).filter(i -> i.createIconForElevator(player, elevator) != null).collect(Collectors.toList());

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> {
            ElevatorHelper.setElevatorEnabled(elevator.getShulkerBox());
            ShulkerBoxHelper.playClose(elevator.getShulkerBox());
        });

        boolean canRename = ElevatorHookService.canRenameElevator(player, elevator, false);

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
            ProtectionHook hook = protectionHooks.get(0);
            ItemStack protectionIcon = hook.createIconForElevator(player, elevator);
            display.setItemSimple(canRename ? 10 : 11, protectionIcon, (event, myDisplay) -> {
                myDisplay.stopReturn();
                hook.onProtectionClick(player, elevator, () -> openInteractMenu(player, elevator));
            });
        }

        display.open();
    }

    public static void openInteractProtectMenu(Player player, Elevator elevator) {
        if (!elevator.isValid()) {
            openInteractMenu(player, elevator);
            return;
        }

        List<ProtectionHook> protectionHooks = ElevatorHookService.getProtectionHooks().stream().filter(i -> i.getConfig().allowCustomization).filter(i -> i.createIconForElevator(player, elevator) != null).collect(Collectors.toList());

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
        if (!elevator.isValid()) {
            openInteractMenu(player, elevator);
            return;
        }

        String currentName = ElevatorDataContainerService.getFloorName(elevator);

        tryOpenSign(player, value -> true,
                result -> {
                    ElevatorDataContainerService.setFloorName(elevator, result);
                    ElevatorGUIHelper.openInteractMenu(player, elevator);
                },
                () -> ElevatorGUIHelper.openInteractMenu(player, elevator),
                ElevatorConfigService.getRootConfig().locale.enterFloorName, true, currentName, ChatColor.BOLD + "^^^^^^^^", "Enter floor", "name above"
        );
    }

    private static List<ElevatorAction> getActionsWithSettings(Elevator elevator, boolean up) {
        List<ElevatorAction> actions = new ArrayList<>(up ? elevator.getElevatorType(false).getActionsUp() : elevator.getElevatorType(false).getActionsDown()); // Don't want to alter the original list.
        actions.removeIf(i -> i.getSettings().isEmpty());
        actions.removeIf(i -> i.getSettings().stream().noneMatch(s -> s.canBeEditedIndividually(elevator)));
        return actions;
    }

    public static void openInteractActionSettingsMenu(Player player, Elevator elevator, ElevatorAction action, Runnable onReturn) {
        if (!elevator.isValid()) {
            onReturn.run();
            return;
        }

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
        if (!elevator.isValid()) {
            openInteractSettingsMenu(player, elevator);
            return;
        }

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
        if (!elevator.isValid()) {
            openInteractMenu(player, elevator);
            return;
        }

        List<ElevatorSetting<?>> settings = ElevatorSettingService.getElevatorSettings().stream().filter(i -> i.canBeEditedIndividually(elevator)).collect(Collectors.toList());

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
