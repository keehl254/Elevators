package me.keehl.elevators.models.settings;

import me.keehl.elevators.helpers.ElevatorGUIHelper;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorConfigService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DisplayNameSetting extends ElevatorSetting<String> {

    public DisplayNameSetting() {
        super("change-display-name","Display Name", "This will change the display of newly created elevators. Old elevators will not be affected.", Material.NAME_TAG, ChatColor.GOLD);
        this.setGetValueGlobal(ElevatorType::getDisplayName);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue) {
        ElevatorGUIHelper.tryOpenAnvil(player, value -> true, result -> {
            elevatorType.setDisplayName(result != null ? result : currentValue);
            returnMethod.run();
        }, returnMethod, ElevatorConfigService.getRootConfig().locale.enterDisplayName, true, currentValue, "Enter new display name.");
    }
}
