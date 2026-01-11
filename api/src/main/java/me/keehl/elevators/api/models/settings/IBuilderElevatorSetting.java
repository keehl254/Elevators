package me.keehl.elevators.api.models.settings;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorSetting;
import me.keehl.elevators.api.models.IElevatorType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

public interface IBuilderElevatorSetting<T> extends IElevatorSetting<T> {

    boolean canBeEditedIndividually(IElevator elevator);

    void onClickGlobal(Player player, IElevatorType apiElevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, T currentValue);

    void onClickIndividual(Player player, IElevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, T currentValue);

    T getGlobalValue(IElevatorType apiElevatorType);

    IElevatorSetting<T> addAction(String action, String description);

}