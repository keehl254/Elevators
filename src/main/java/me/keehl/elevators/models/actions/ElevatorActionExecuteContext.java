package me.keehl.elevators.models.actions;

import me.keehl.elevators.api.models.IElevatorActionVariable;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.actions.IElevatorActionExecuteContext;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ElevatorActionExecuteContext implements IElevatorActionExecuteContext {

        private final ElevatorAction action;
        private final IElevatorEventData eventData;
        private final Player player;

        protected ElevatorActionExecuteContext(ElevatorAction action, IElevatorEventData eventData, Player player) {
            this.action = action;
            this.eventData = eventData;
            this.player = player;
        }

        @SuppressWarnings("unchecked")
        public <T> T getVariable(String alias) {
            Optional<IElevatorActionVariable<?>> groupingOptional = this.action.getGroupingByAlias(alias);
            if(groupingOptional.isEmpty())
                throw new RuntimeException("Attempt to pull Elevator Action Variable with alias that was not setup: " + this.action.getKey() + " -> " + alias);
            return (T) this.action.getVariableValue(groupingOptional.get(), this.eventData.getOrigin());
        }

        public ElevatorAction getAction() {
            return this.action;
        }

        public IElevatorEventData getEventData() {
            return this.eventData;
        }

        public Player getPlayer() {
            return this.player;
        }

    }