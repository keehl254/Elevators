package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.ElevatorType;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DisplayNameSetting extends ElevatorSetting<String> {

    public DisplayNameSetting() {
        super("change-display-name","Display Name", "This will change the display of newly created elevators. Old elevators will not be affected.", Material.NAME_TAG, ChatColor.GOLD);
        this.setGetValueGlobal(ElevatorType::getDisplayName);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue) {
        AnvilGUI.Builder anvilBuilder = new AnvilGUI.Builder();
        anvilBuilder.preventClose();
        anvilBuilder.text(currentValue);
        anvilBuilder.title("Enter new display name.");
        anvilBuilder.plugin(Elevators.getInstance());
        anvilBuilder.onClose(state -> {
            elevatorType.setDisplayName(state.getText());
            returnMethod.run();
        });
        anvilBuilder.onClick((slot, state) -> {
           if (slot != AnvilGUI.Slot.OUTPUT)
               return Collections.emptyList();
           return List.of(AnvilGUI.ResponseAction.close());
        });
        anvilBuilder.open(player);
    }
}
