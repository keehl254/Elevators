package me.keehl.elevators.api.models.settings;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorSetting;
import me.keehl.elevators.api.models.IElevatorType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public interface IBuilderElevatorSetting<T> extends IElevatorSetting<T> {

    boolean canBeEditedIndividually(@NotNull IElevator elevator);

    void onClickGlobal(@NotNull Player player, @NotNull IElevatorType apiElevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull T currentValue);

    void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull T currentValue);

    @NotNull T getGlobalValue(@NotNull IElevatorType apiElevatorType);

    @NotNull IElevatorSetting<T> addAction(@NotNull String action, @NotNull String description);

}