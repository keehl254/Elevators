package me.keehl.elevators.models.hooks;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.ElevatorDataContainerService;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigHookData;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ProtectionHook implements ElevatorHook {

    private final String configKey;
    private final NamespacedKey containerKey;
    public ProtectionHook(String configKey) {
        this.configKey = configKey;
        this.containerKey = ElevatorDataContainerService.getKeyFromKey("protection-" + configKey, ElevatorDataContainerService.booleanPersistentDataType);

        ElevatorConfigService.addConfigCallback(root -> getConfig());
    }

    public ConfigHookData getConfig() {

        if (!ElevatorConfigService.getRootConfig().protectionHooks.containsKey(this.configKey))
            ElevatorConfigService.getRootConfig().protectionHooks.put(this.configKey, new ConfigHookData());
        return ElevatorConfigService.getRootConfig().protectionHooks.get(this.configKey);
    }

    public boolean isCheckEnabled(Elevator elevator) {
        return ElevatorDataContainerService.getElevatorValue(elevator.getShulkerBox(), this.containerKey, getConfig().blockNonMemberUseDefault);
    }

    public void toggleCheckEnabled(Elevator elevator) {
        boolean currentValue = this.isCheckEnabled(elevator);
        ElevatorDataContainerService.setElevatorValue(elevator.getShulkerBox(), this.containerKey, !currentValue);
        elevator.getShulkerBox().update();
    }

    public String getConfigKey() {
        return this.configKey;
    }

    public abstract void onProtectionClick(Player player, Elevator elevator, Runnable onReturn);

    public abstract boolean canEditName(Player player, Elevator elevator, boolean sendMessage);

    public abstract boolean canEditSettings(Player player, Elevator elevator, boolean sendMessage);

    public abstract boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage);

    public abstract ItemStack createIconForElevator(Player player, Elevator elevator);
}
