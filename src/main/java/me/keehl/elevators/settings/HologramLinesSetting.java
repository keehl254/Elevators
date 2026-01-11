package me.keehl.elevators.settings;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.services.IElevatorDataContainerService;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import me.keehl.elevators.helpers.*;
import me.keehl.elevators.services.interaction.PagedDisplay;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class HologramLinesSetting extends InternalElevatorSetting<ILocaleComponent[]> {

    public HologramLinesSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.HOLO_LINES.getSettingName(), "Hologram Lines", "Click to alter the hologram lines that appear above the elevator.", Material.PAPER, ChatColor.YELLOW);
        this.setupDataStore("hologram-lines", IElevatorDataContainerService.localeComponentArrayPersistentDataType);
        this.addAction("Left Click", "Edit Text");
    }

    private void addLine(Player player, ILocaleComponent[] currentValue, Consumer<ILocaleComponent[]> completeConsumer) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.onComplete(result -> {
            if (result == null) {
                completeConsumer.accept(currentValue);
                return SimpleInput.SimpleInputResult.STOP;
            }
            ILocaleComponent[] lines = Arrays.copyOf(currentValue, currentValue.length + 1);
            lines[lines.length - 1] = MessageHelper.getLocaleComponent(result);

            completeConsumer.accept(lines);
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(() -> completeConsumer.accept(currentValue));
        Elevators.getLocale().getEnterMessageMessage().send(player);
        input.start();
    }

    private void editLines(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, ILocaleComponent[] currentValue, Consumer<ILocaleComponent[]> setValueMethod) {

        List<ILocaleComponent> messages = new ArrayList<>(Arrays.asList(currentValue));

        PagedDisplay<ILocaleComponent> display = new PagedDisplay<>(Elevators.getInstance(), player, messages, "Admin > Settings > Hologram", returnMethod);
        List<Material> materials = new ArrayList<>(TagHelper.ITEMS_BOOKSHELF_BOOKS.getValues());
        display.onCreateItem(message -> {
            int hashCode = Math.abs(message.hashCode());
            Material book = materials.get((hashCode % materials.size()));
            ChatColor color = ChatColor.getByChar(Integer.toHexString(hashCode % 16));
            if (color == ChatColor.BLACK)
                color = ChatColor.GOLD;

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Value: " + ChatColor.GRAY + message.toLegacyText());
            lore.add("");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "Move up line");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Right Click: " + ChatColor.GRAY + "Move back line");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Shift Click: " + ChatColor.GRAY + "Delete line");

            return ItemStackHelper.createItem(color + "" + ChatColor.BOLD + "Line " + (messages.indexOf(message) + 1), book, 1, lore);
        });
        display.onClick((item, event, myDisplay) -> {

            int currentIndex = messages.indexOf(item);

            ILocaleComponent[] value = messages.toArray(new ILocaleComponent[]{});
            if (event.isShiftClick()) {
                display.stopReturn();

                ElevatorMenuHelper.openConfirmMenu(player, confirm -> {
                    ILocaleComponent[] newValue = messages.toArray(new ILocaleComponent[]{});
                    if(confirm) {
                        messages.remove(item);
                        newValue = messages.toArray(new ILocaleComponent[]{});
                        setValueMethod.accept(newValue);
                    }
                    editLines(player, returnMethod, clickEvent, newValue, setValueMethod);
                });
                return;
            } else if (event.isLeftClick()) {
                if (currentIndex > 0) {
                    ILocaleComponent priorMessage = value[currentIndex - 1];
                    value[currentIndex - 1] = item;
                    value[currentIndex] = priorMessage;
                }
            } else if (event.isRightClick()) {
                if (currentIndex < messages.size() - 1) {
                    ILocaleComponent afterMessage = value[currentIndex + 1];
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
    public void onClickGlobal(Player player, IElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, ILocaleComponent[] currentValue) {
        this.editLines(player, returnMethod, clickEvent, currentValue, (value) -> elevatorType.setHologramLines(Arrays.asList(value)));
    }

    @Override
    public void onClickIndividual(Player player, IElevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, ILocaleComponent[] currentValue) {
        this.editLines(player, returnMethod, clickEvent, currentValue, (value) -> this.setIndividualValue(elevator, value));
    }

    @Override
    public ILocaleComponent[] getGlobalValue(IElevatorType elevatorType) {
        return elevatorType.getHolographicLines().toArray(new ILocaleComponent[]{});
    }

    @Override
    public boolean canBeEditedIndividually(IElevator elevator) {
        return true;
    }

    @Override
    public ItemStack createIcon(Object value, boolean global) {
        List<String> lore = new ArrayList<>();
        ILocaleComponent[] loreLines = (ILocaleComponent[]) value;

        ItemMeta templateMeta = this.iconTemplate.getItemMeta();
        if (templateMeta.hasLore())
            lore.addAll(Objects.requireNonNull(templateMeta.getLore()));

        lore.add("");
        if (loreLines.length == 0) {
            lore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + ChatColor.BOLD + "None");
        } else {
            lore.add(ChatColor.GRAY + "Current Value: ");
            for (ILocaleComponent line : loreLines)
                lore.add(ChatColor.WHITE + line.toLegacyText());
        }

        ItemStack icon = this.iconTemplate.clone();
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setLore(lore);
        icon.setItemMeta(iconMeta);

        return icon;
    }

}
