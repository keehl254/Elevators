package me.keehl.elevators.menus.admin;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.helpers.ElevatorMenuHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.services.interaction.PagedDisplay;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class AdminActionsMenu {

    public static void openAdminActionsMenu(Player player, IElevatorType tempElevatorType, List<IElevatorAction> actions) {
        final IElevatorType elevatorType = Elevators.getElevatorTypeService().getElevatorType(tempElevatorType.getTypeKey());
        if (elevatorType == null) {
            player.closeInventory();
            return;
        }

        PagedDisplay<IElevatorAction> display = new PagedDisplay<>(Elevators.getInstance(), player, actions, "Admin > Settings > Actions", () -> AdminSettingsMenu.openAdminSettingsMenu(player, elevatorType));
        display.onCreateItem(action -> {
            ItemStack template = action.getIcon().clone();
            ItemMeta meta = template.getItemMeta();
            assert meta != null;

            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add("");
            if (!action.getSettings().isEmpty())
                lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Left Click: " + ChatColor.GRAY + "Edit Action");
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Shift Click: " + ChatColor.GRAY + "Delete Action");

            meta.setLore(lore);
            template.setItemMeta(meta);
            return template;
        });
        display.onClick((action, event, myDisplay) -> {
            myDisplay.stopReturn();

            if (event.isShiftClick()) {
                ElevatorMenuHelper.openConfirmMenu(player, confirm -> {
                    if (confirm)
                        actions.remove(action);

                    openAdminActionsMenu(player, elevatorType, actions);
                });
                return;
            }

            if (!action.getSettings().isEmpty())
                AdminActionSettingsMenu.openAdminActionSettingsMenu(player, elevatorType, action, () -> openAdminActionsMenu(player, elevatorType, actions));
        });
        display.onLoad((tempDisplay, page) -> {
            int addRecipeIndex = display.getDisplay().getInventory().getSize() - 1;
            display.getDisplay().setItemSimple(addRecipeIndex, ItemStackHelper.createItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Add Action", Material.NETHER_STAR, 1), (event, myDisplay) -> {
                myDisplay.stopReturn();
                AdminCreateActionMenu.openAdminCreateActionMenu(player, elevatorType, actions);
            });
        });

        display.open();
    }
}
