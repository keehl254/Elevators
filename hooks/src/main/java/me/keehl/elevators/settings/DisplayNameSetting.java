package me.keehl.elevators.settings;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.interaction.SimpleInput;
import me.keehl.elevators.util.InternalElevatorSettingType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DisplayNameSetting extends InternalElevatorSetting<String> {

    public DisplayNameSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.DISPLAY_NAME.getSettingName(),"Display Name", "This will change the display of newly created elevators. Old elevators will not be affected.", Material.NAME_TAG, ChatColor.GOLD);
        this.addAction("Left Click", "Edit Text");
    }

    @Override
    public boolean canBeEditedIndividually(Elevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.allowReset();
        input.onComplete(result -> {
            elevatorType.setDisplayName(result != null ? result : currentValue);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterDisplayName);
        input.start();

    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue) {
        returnMethod.run();
    }

    @Override
    public String getGlobalValue(ElevatorType elevatorType) {
        return elevatorType.getDisplayName();
    }
}
