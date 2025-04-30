package com.lkeehl.elevators.models.hooks;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.services.DataContainerService;
import com.lkeehl.elevators.services.configs.ConfigHookData;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public abstract class ProtectionHook implements ElevatorHook {

    private final String configKey;
    private final NamespacedKey containerKey;
    public ProtectionHook(String configKey) {
        this.configKey = configKey;
        this.containerKey = DataContainerService.getKeyFromKey("protection-" + configKey, PersistentDataType.BOOLEAN);

        ConfigService.addConfigCallback(root -> getConfig());
    }

    public ConfigHookData getConfig() {

        if (!ConfigService.getRootConfig().protectionHooks.containsKey(this.configKey))
            ConfigService.getRootConfig().protectionHooks.put(this.configKey, new ConfigHookData());
        return ConfigService.getRootConfig().protectionHooks.get(this.configKey);
    }

    public boolean isCheckEnabled(Elevator elevator) {
        return DataContainerService.getElevatorValue(elevator.getShulkerBox(), this.containerKey, getConfig().blockNonMemberUseDefault);
    }

    public void toggleCheckEnabled(Elevator elevator) {
        boolean currentValue = this.isCheckEnabled(elevator);
        DataContainerService.setElevatorValue(elevator.getShulkerBox(), this.containerKey, !currentValue);
        elevator.getShulkerBox().update();
    }

    public abstract void onProtectionClick(Player player, Elevator elevator, Runnable onReturn);

    public abstract boolean canEditName(Player player, Elevator elevator, boolean sendMessage);

    public abstract boolean canEditSettings(Player player, Elevator elevator, boolean sendMessage);
}
