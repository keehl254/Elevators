package me.keehl.elevators.settings;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import me.keehl.elevators.services.interaction.SimpleInput;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class UsePermissionSetting extends InternalElevatorSetting<String> {

    public UsePermissionSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.USE_PERMISSION.getSettingName(),"Use Permission", "This will change the permission required to use the elevator.", Material.BEACON, ChatColor.GOLD);
        this.addAction("Left Click", "Edit Permission");
    }

    @Override
    public boolean canBeEditedIndividually(IElevator elevator) {
        return false;
    }

    @Override
    public void onClickGlobal(Player player, IElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue) {
        player.closeInventory();

        SimpleInput input = new SimpleInput(Elevators.getInstance(), player);
        input.allowReset();

        input.onComplete(result -> {

            if (result == null)
                result = "elevators.use." + elevatorType.getTypeKey();

            elevatorType.setUsePermission(result);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        Elevators.getLocale().getEnterUsePermissionMessage().send(player);
        input.start();
    }

    @Override
    public void onClickIndividual(Player player, IElevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue) {
        returnMethod.run();
    }

    @Override
    public String getGlobalValue(IElevatorType elevatorType) {
        return elevatorType.getUsePermission();
    }
}
