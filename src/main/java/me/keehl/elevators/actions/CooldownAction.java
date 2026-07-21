package me.keehl.elevators.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorActionSetting;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.models.actions.ElevatorAction;
import me.keehl.elevators.models.actions.ElevatorActionVariable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CooldownAction extends ElevatorAction {

    private static final ElevatorActionVariable<Long> timespanGrouping = new ElevatorActionVariable<>(1000L, Long::parseLong, "timespan", "time", "t");

    public CooldownAction(JavaPlugin plugin, IElevatorType elevatorType, String key) {
        super(plugin, elevatorType, key, timespanGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the timespan of the elevator cooldown in milliseconds.";
        IElevatorActionSetting<Long> setting = this.mapSetting(timespanGrouping, "timespan", "Timespan", desc, Material.CLOCK, ChatColor.GOLD, false);
        setting.onClick(this::editTimespan);
        setting.addAction("Left Click", "Add 500 MS");
        setting.addAction("Right Click", "Subtract 500 MS");
    }

    @Override
    public void execute(@NotNull IElevatorEventData eventData, @NotNull Player player) {
        if (eventData.getOrigin() == null)
            return;

        String key = "elevator-cooldown-" + player.getUniqueId() + "-"+this.getIdentifier();

        ShulkerBox box = eventData.getOrigin().getShulkerBox();
        box.setMetadata(key, new FixedMetadataValue(Elevators.getInstance(), System.currentTimeMillis()));
    }

    @Override
    public boolean meetsConditions(@NotNull IElevatorEventData eventData, @NotNull Player player) {
        if (eventData.getOrigin() == null)
            return true;

        long timespan = this.getVariableValue(timespanGrouping, eventData.getOrigin());
        String key = "elevator-cooldown-" + player.getUniqueId() + "-"+this.getIdentifier();

        ShulkerBox box = eventData.getOrigin().getShulkerBox();
        if (box.hasMetadata(key)) {
            MetadataValue value = box.getMetadata(key).getFirst();
            long lastTime = value.asLong();
            if (System.currentTimeMillis() - lastTime < timespan) {
                if (!ElevatorHelper.hasOrAddPlayerCoolDown(player, "message", 1000)) { // Cooldown for a cooldown. Interesting.
                    Elevators.getLocale().getCooldownMessage().sendFormatted(player, eventData);
                }
                return false;
            }
            box.removeMetadata(key, Elevators.getInstance());
        }
        return true;
    }

    private void editTimespan(final Player player, final Runnable returnMethod, final InventoryClickEvent clickEvent, final long currentValue, final Consumer<Long> setValueMethod) {
        long newValue = currentValue + (clickEvent.isLeftClick() ? 500 : -500);
        setValueMethod.accept(Math.max(newValue, 500));
        returnMethod.run();
    }

}
