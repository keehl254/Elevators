package me.keehl.elevators.api.models.settings;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface IElevatorSettingClickContext<T> {

    void setValue(T newValue);

    void close();

    T getCurrentValue();

    InventoryClickEvent getClickEvent();

    Player getPlayer();

}
