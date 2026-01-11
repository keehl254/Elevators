package me.keehl.elevators.api.models;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public interface IElevatorSetting<T> {

    IElevatorSetting<T> addAction(String action, String description);

    boolean isSettingGlobalOnly(IElevator elevator);

    boolean canBeEditedIndividually(IElevator elevator);

    ItemStack createIcon(Object value, boolean global);

    void clickGlobal(Player player, IElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent);

    void clickIndividual(Player player, IElevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent);

    void onClickGlobal(Player player, IElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, T currentValue);

    void onClickIndividual(Player player, IElevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, T currentValue);

    T getGlobalValue(IElevatorType elevatorType);


    T getIndividualValue(IElevator elevator);

    void setIndividualValue(IElevator elevator, T value);

    String getSettingName();

    JavaPlugin getPlugin();

}
