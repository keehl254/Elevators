package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ColorHelper;
import com.lkeehl.elevators.helpers.ElevatorGUIHelper;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.services.DataContainerService;
import com.lkeehl.elevators.services.interaction.PagedDisplay;
import com.lkeehl.elevators.services.interaction.SimpleDisplay;
import com.lkeehl.elevators.services.interaction.SimpleInput;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class HologramLinesSetting extends ElevatorSetting<String[]> {

    public HologramLinesSetting() {
        super("change-holo", "Hologram Lines", "Click to alter the hologram lines that appear above the elevator.", Material.PAPER, ChatColor.YELLOW);
        this.setGetValueGlobal(e -> e.getHolographicLines().toArray(new String[]{}));
        this.setupDataStore("hologram-lines", DataContainerService.stringArrayPersistentDataType);
    }

    private void addLine(Player player, String[] currentValue, Consumer<String[]> completeConsumer) {
        player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(result -> {
            if (result == null) {
                completeConsumer.accept(currentValue);
                return SimpleInput.SimpleInputResult.STOP;
            }
            String[] lines = Arrays.copyOf(currentValue, currentValue.length + 1);
            lines[lines.length - 1] = result;

            completeConsumer.accept(lines);
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(() -> completeConsumer.accept(currentValue));
        MessageHelper.sendFormattedMessage(player, ConfigService.getRootConfig().locale.enterMessage);
        input.start();
    }

    private void editLines(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String[] currentValue, Consumer<String[]> setValueMethod) {

        List<String> messages = new ArrayList<>(Arrays.asList(currentValue));

        PagedDisplay<String> display = new PagedDisplay<>(Elevators.getInstance(), player, messages, "Admin > Settings > Hologram", returnMethod);
        List<Material> materials = new ArrayList<>(Tag.ITEMS_BOOKSHELF_BOOKS.getValues());
        display.onCreateItem(message -> {
            int hashCode = Math.abs(message.hashCode());
            Material book = materials.get((hashCode % materials.size()));
            ChatColor color = ChatColor.getByChar(Integer.toHexString(hashCode % 16));
            if (color == ChatColor.BLACK)
                color = ChatColor.GOLD;

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Value: " + ChatColor.GRAY + MessageHelper.formatColors(message));
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "Move up line");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Right Click: " + ChatColor.GRAY + "Move back line");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Shift Click: " + ChatColor.GRAY + "Delete line");

            return ItemStackHelper.createItem(color + "" + ChatColor.BOLD + "Line " + (messages.indexOf(message) + 1), book, 1, lore);
        });
        display.onClick((item, event, myDisplay) -> {

            int currentIndex = messages.indexOf(item);

            String[] value = messages.toArray(String[]::new);
            if (event.isShiftClick()) {
                display.stopReturn();

                ElevatorGUIHelper.openConfirmMenu(player, confirm -> {
                    String[] newValue = messages.toArray(String[]::new);
                    if(confirm) {
                        messages.remove(item);
                        newValue = messages.toArray(String[]::new);
                        setValueMethod.accept(newValue);
                    }
                    editLines(player, returnMethod, clickEvent, newValue, setValueMethod);
                });
                return;
            } else if (event.isLeftClick()) {
                if (currentIndex > 0) {
                    String priorMessage = value[currentIndex - 1];
                    value[currentIndex - 1] = item;
                    value[currentIndex] = priorMessage;
                }
            } else if (event.isRightClick()) {
                if (currentIndex < messages.size() - 1) {
                    String afterMessage = value[currentIndex + 1];
                    value[currentIndex + 1] = item;
                    value[currentIndex] = afterMessage;
                }
            }

            setValueMethod.accept(value);
            myDisplay.stopReturn();
            editLines(player, returnMethod, clickEvent, value, setValueMethod);
        });


        display.onLoad((tempDisplay, page) -> {
            int addIndex = display.getDisplay().getInventory().getSize() - 1;
            display.getDisplay().setItemSimple(addIndex, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Add Line", Material.NETHER_STAR, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                addLine(player, currentValue, (value) -> {
                    setValueMethod.accept(value);
                    editLines(player, returnMethod, clickEvent, value, setValueMethod);
                });
            });
        });

        display.open();
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, String[] currentValue) {
        this.editLines(player, returnMethod, clickEvent, currentValue, (value) -> elevatorType.setHologramLines(Arrays.asList(value)));
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, String[] currentValue) {
        this.editLines(player, returnMethod, clickEvent, currentValue, (value) -> this.setIndividualElevatorValue(elevator, value));
    }

    @Override
    public ItemStack createIcon(Object value, boolean global) {
        List<String> lore = new ArrayList<>();
        String[] loreLines = (String[]) value;

        ItemMeta templateMeta = this.iconTemplate.getItemMeta();
        if (templateMeta.hasLore())
            lore.addAll(Objects.requireNonNull(templateMeta.getLore()));

        lore.add("");
        if (loreLines.length == 0) {
            lore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + "" + ChatColor.BOLD + "None");
        } else {
            lore.add(ChatColor.GRAY + "Current Value: ");
            for (String line : loreLines)
                lore.add("\t" + ChatColor.GOLD + "" + MessageHelper.formatColors(line));
        }

        ItemStack icon = this.iconTemplate.clone();
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setLore(lore);
        icon.setItemMeta(iconMeta);

        return icon;
    }

}
