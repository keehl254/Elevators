package me.keehl.elevators.api.models.hooks;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.services.configs.versions.IConfigHookData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IProtectionHook extends ElevatorHook {

    IConfigHookData getConfig();

    boolean isCheckEnabled(IElevator elevator);

    void toggleCheckEnabled(IElevator elevator);

    String getConfigKey();

    void onProtectionClick(Player player, IElevator elevator, Runnable onReturn);

    boolean canEditName(Player player, IElevator elevator, boolean sendMessage);

    boolean canEditSettings(Player player, IElevator elevator, boolean sendMessage);

    boolean canPlayerUseElevator(Player player, IElevator elevator, boolean sendMessage);

    ItemStack createIconForElevator(Player player, IElevator elevator);
}
