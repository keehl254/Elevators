package me.keehl.elevators.models.actions;

import me.keehl.elevators.api.models.settings.IElevatorSettingClickContext;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class ElevatorSettingClickContext<T> implements IElevatorSettingClickContext<T> {

    private final Player player;

    private final Runnable returnMethod;

    private final InventoryClickEvent clickEvent;

    private final T currentValue;

    private final Consumer<T> setValueConsumer;

    public ElevatorSettingClickContext(Player player, Runnable returnMethod, InventoryClickEvent event, T currentValue, Consumer<T> setValueMethod) {
        this.player = player;
        this.returnMethod = returnMethod;
        this.clickEvent = event;
        this.currentValue = currentValue;
        this.setValueConsumer = setValueMethod;
    }

    public void setValue(T newValue) {
        this.setValueConsumer.accept(newValue);
    }

    public void close() {
        this.returnMethod.run();
    }

    public T getCurrentValue() {
        return this.currentValue;
    }

    public InventoryClickEvent getClickEvent() {
        return this.clickEvent;
    }

    public Player getPlayer() {
        return this.player;
    }

}
