package me.keehl.elevators.settings;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DisplayNameSetting extends InternalElevatorSetting<ILocaleComponent> {

    public DisplayNameSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.DISPLAY_NAME.getSettingName(),"Display Name", "This will change the display of newly created elevators. Old elevators will not be affected.", Material.NAME_TAG, ChatColor.GOLD);
        this.addAction("Left Click", "Edit Text");
    }

    @Override
    public boolean canBeEditedIndividually(IElevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(Player player, IElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, ILocaleComponent currentValue) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.allowReset();
        input.onComplete(result -> {
            elevatorType.setDisplayName(result != null ? MessageHelper.getLocaleComponent(result) : currentValue);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        Elevators.getLocale().getEnterDisplayNameMessage().send(player);
        input.start();

    }

    @Override
    public void onClickIndividual(Player player, IElevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, ILocaleComponent currentValue) {
        returnMethod.run();
    }

    @Override
    public ILocaleComponent getGlobalValue(IElevatorType elevatorType) {
        return elevatorType.getDisplayName();
    }
}
