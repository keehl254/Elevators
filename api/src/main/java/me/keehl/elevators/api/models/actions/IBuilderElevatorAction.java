package me.keehl.elevators.api.models.actions;

import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorEventData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface IBuilderElevatorAction extends IElevatorAction {

    void onInitialize(String value);

    void execute(@NotNull IElevatorEventData eventData, @NotNull Player player);

    boolean meetsConditions(@NotNull IElevatorEventData eventData, @NotNull Player player);
}