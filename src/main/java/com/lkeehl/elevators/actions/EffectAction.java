package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.*;
import com.lkeehl.elevators.services.ElevatorEffectService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class EffectAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> effectNameGrouping = new ElevatorActionGrouping<>("CREEPER", i -> i, "name","n");

    public EffectAction(ElevatorType elevatorType) {
        super(elevatorType, "effect","name", effectNameGrouping);
    }

    @Override
    protected void onInitialize(String value) {

    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        ElevatorEffect effect = ElevatorEffectService.getEffectFromKey(this.getGroupingObject(effectNameGrouping));
        if(effect == null)
            return;

        effect.playEffect(eventData);
    }
}
