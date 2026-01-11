package me.keehl.elevators.menus.interact;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.hooks.IProtectionHook;
import me.keehl.elevators.helpers.*;
import me.keehl.elevators.models.ElevatorEventData;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

import static me.keehl.elevators.helpers.ElevatorMenuHelper.openMenuFromDisplay;

@SuppressWarnings("deprecation")
public class InteractMenu {

    public static void openInteractMenu(Player player, IElevator elevator) {
        if (!elevator.isValid()) {
            Elevators.getLocale().getElevatorChangedKickedOutMessage().sendFormatted(player, new ElevatorEventData(player, elevator, elevator, (byte) 1, 0));
            if (elevator.getShulkerBox() != null && ElevatorHelper.isElevatorDisabled(elevator.getShulkerBox())) {
                ElevatorHelper.setElevatorEnabled(elevator.getShulkerBox());
                ShulkerBoxHelper.playClose(elevator.getShulkerBox());
            }
            elevator.getShulkerBox().removeMetadata("open-player", Elevators.getInstance());
            player.closeInventory();
            return;
        }
        elevator.getShulkerBox().setMetadata("open-player", new FixedMetadataValue(Elevators.getInstance(), player.getUniqueId().toString()));

        Inventory inventory = Bukkit.createInventory(null, 27, "Elevator");

        ElevatorHelper.setElevatorDisabled(elevator.getShulkerBox());
        ShulkerBoxHelper.playOpen(elevator.getShulkerBox());

        ElevatorMenuHelper.fillEmptySlotsWithPanes(inventory, elevator.getDyeColor());

        List<String> nameLore = new ArrayList<>();
        nameLore.add("");
        nameLore.add(ChatColor.GRAY + "Current Value: ");
        nameLore.add(ChatColor.GOLD + "" + ChatColor.BOLD + Elevators.getDataContainerService().getFloorName(elevator));

        ItemStack protectionItem = ItemStackHelper.createItem(ChatColor.RED + "" + ChatColor.BOLD + "Protection", Material.DIAMOND_SWORD, 1);
        ItemStack nameItem = ItemStackHelper.createItem(ChatColor.YELLOW + "" + ChatColor.BOLD + "Floor Name", Material.NAME_TAG, 1, nameLore);
        ItemStack settingsItem = ItemStackHelper.createItem(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Settings", Material.SEA_LANTERN, 1);

        List<IProtectionHook> protectionHooks = Elevators.getHooksService().getProtectionHooks().stream().filter(i -> i.getConfig().doesAllowCustomization()).filter(i -> i.createIconForElevator(player, elevator) != null).toList();

        SimpleDisplay display = new SimpleDisplay(Elevators.getInstance(), player, inventory, () -> {
            ElevatorHelper.setElevatorEnabled(elevator.getShulkerBox());
            ShulkerBoxHelper.playClose(elevator.getShulkerBox());

            elevator.getShulkerBox().removeMetadata("open-player", Elevators.getInstance());
        });

        boolean canRename = Elevators.getHooksService().canRenameElevator(player, elevator, false);

        if (protectionHooks.isEmpty()) {

            if (canRename) {
                display.setItemSimple(11, nameItem, (event, myDisplay) -> InteractNameMenu.openInteractNameMenu(myDisplay, player, elevator));
                display.setItemSimple(15, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InteractSettingsMenu::openInteractSettingsMenu));
            } else {
                display.setItemSimple(13, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InteractSettingsMenu::openInteractSettingsMenu));
            }
            display.open();
            return;
        }

        if (canRename) {
            display.setItemSimple(10, protectionItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InteractProtectMenu::openInteractProtectMenu));
            display.setItemSimple(13, nameItem, (event, myDisplay) -> InteractNameMenu.openInteractNameMenu(myDisplay, player, elevator));
            display.setItemSimple(16, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InteractSettingsMenu::openInteractSettingsMenu));
        } else {
            display.setItemSimple(11, protectionItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InteractProtectMenu::openInteractProtectMenu));
            display.setItemSimple(15, settingsItem, (event, myDisplay) -> openMenuFromDisplay(myDisplay, player, elevator, InteractSettingsMenu::openInteractSettingsMenu));
        }

        if (protectionHooks.size() == 1) {
            IProtectionHook hook = protectionHooks.getFirst();
            ItemStack protectionIcon = hook.createIconForElevator(player, elevator);
            display.setItemSimple(canRename ? 10 : 11, protectionIcon, (event, myDisplay) -> {
                myDisplay.stopReturn();
                hook.onProtectionClick(player, elevator, () -> InteractMenu.openInteractMenu(player, elevator));
            });
        }

        display.open();
    }

}
