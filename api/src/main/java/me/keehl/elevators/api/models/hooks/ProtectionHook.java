package me.keehl.elevators.api.models.hooks;

import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.services.configs.versions.IConfigHookData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ProtectionHook<T extends IConfigHookData> implements IProtectionHook {


    private final String configKey;

    private final T defaultConfig;

    public ProtectionHook(String configKey, T defaultConfig) {
        this(configKey, true, defaultConfig);
    }
    public ProtectionHook(String configKey, boolean blockGuestsByDefault, T defaultConfig) {
        this.configKey = configKey;

        this.defaultConfig = defaultConfig;
        this.defaultConfig.setBlockNonMemberUseByDefault(blockGuestsByDefault);
    }

    public T getConfig() {
        return ElevatorsAPI.getElevators().getElevatorProtectionHookConfig(this, this.defaultConfig);
    }

    public boolean isCheckEnabled(IElevator elevator) {
        return this.getConfig().doesBlockNonMemberUseByDefault();
    }

    public void toggleCheckEnabled(IElevator elevator) {
        ElevatorsAPI.getElevators().toggleElevatorProtectionHook(elevator, this);
    }

    public String getConfigKey() {
        return this.configKey;
    }

    public final boolean doesBlockGuestsByDefault() {
        return this.defaultConfig.doesBlockNonMemberUseByDefault();
    }

    public abstract void onProtectionClick(Player player, IElevator elevator, Runnable onReturn);

    public abstract boolean canEditName(Player player, IElevator elevator, boolean sendMessage);

    public abstract boolean canEditSettings(Player player, IElevator elevator, boolean sendMessage);

    public abstract boolean canPlayerUseElevator(Player player, IElevator elevator, boolean sendMessage);

    public abstract ItemStack createIconForElevator(Player player, IElevator elevator);
}
