package me.keehl.elevators.api.models;

import me.keehl.elevators.api.util.PentaConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public interface IElevatorActionSetting<T> extends IElevatorSetting<String> {

    void onClick(PentaConsumer<Player, Runnable, InventoryClickEvent, T, Consumer<T>> setValueGlobalMethod);

    void onClickGlobal(Player player, IElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue);

    void onClickIndividual(Player player, IElevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue);

    String getGlobalValue(IElevatorType elevatorType);

    IElevatorSetting<String> setupDataStore(String settingKey, PersistentDataType<?, String> dataType);

    boolean canBeEditedIndividually(IElevator elevator);


}
