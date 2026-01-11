package me.keehl.elevators.api.models.actions;

import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorEventData;
import org.bukkit.entity.Player;

public interface IElevatorActionExecuteContext {

    <T> T getVariable(String alias);

    IElevatorAction getAction();

    IElevatorEventData getEventData();

    Player getPlayer();
}