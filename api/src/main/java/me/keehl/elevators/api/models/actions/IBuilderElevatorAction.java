package me.keehl.elevators.api.models.actions;

import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorEventData;
import org.bukkit.entity.Player;

public interface IBuilderElevatorAction extends IElevatorAction {

    void onInitialize(String value);

    void execute(IElevatorEventData eventData, Player player);

    boolean meetsConditions(IElevatorEventData eventData, Player player);
}