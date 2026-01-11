package me.keehl.elevators.api.models.hooks;

import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.services.configs.versions.IConfigHookData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ProtectionHook implements IProtectionHook {

    private final String configKey;
    public ProtectionHook(String configKey) {
        this.configKey = configKey;
    }

    public IConfigHookData getConfig() {
        return ElevatorsAPI.getElevatorProtectionHookConfig(this);
    }

    public boolean isCheckEnabled(IElevator elevator) {
        return ElevatorsAPI.isElevatorProtectionHookCheckEnabled(elevator, this);
    }

    public void toggleCheckEnabled(IElevator elevator) {
        ElevatorsAPI.toggleElevatorProtectionHook(elevator, this);
    }

    public String getConfigKey() {
        return this.configKey;
    }

    public abstract void onProtectionClick(Player player, IElevator elevator, Runnable onReturn);

    public abstract boolean canEditName(Player player, IElevator elevator, boolean sendMessage);

    public abstract boolean canEditSettings(Player player, IElevator elevator, boolean sendMessage);

    public abstract boolean canPlayerUseElevator(Player player, IElevator elevator, boolean sendMessage);

    public abstract ItemStack createIconForElevator(Player player, IElevator elevator);
}
