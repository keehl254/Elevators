package me.keehl.elevators.api.services;

import me.keehl.elevators.api.models.IElevatorEffect;

import java.util.List;

public interface IElevatorEffectsService extends IElevatorService {

    IElevatorEffect getEffectFromKey(String effectKey);

    void registerVisualEffect(IElevatorEffect effect);

    List<IElevatorEffect> getEffects();

}
