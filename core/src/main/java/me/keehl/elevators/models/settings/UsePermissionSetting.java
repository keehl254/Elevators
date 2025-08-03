package me.keehl.elevators.models.settings;

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

public class UsePermissionSetting extends InternalElevatorSetting<String> {

    public UsePermissionSetting(JavaPlugin plugin) {
        super(plugin, InternalElevatorSettingType.USE_PERMISSION.getSettingName(),"Use Permission", "This will change the permission required to use the elevator.", Material.BEACON, ChatColor.GOLD);
        this.addAction("Left Click", "Edit Permission");
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

            if (result == null)
                result = "elevators.use." + elevatorType.getTypeKey();

            elevatorType.setUsePermission(result);
            returnMethod.run();
            return SimpleInput.SimpleInputResult.STOP;
        });
        input.onCancel(returnMethod);
        MessageHelper.sendFormattedMessage(player, ElevatorConfigService.getRootConfig().locale.enterUsePermission);
        input.start();
    }

    @Override
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue) {
        returnMethod.run();
    }

    @Override
    public String getGlobalValue(ElevatorType elevatorType) {
        return elevatorType.getUsePermission();
    }
}
