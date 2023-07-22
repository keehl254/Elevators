package com.lkeehl.elevators.effects;

import com.lkeehl.elevators.models.ElevatorEffect;
import com.lkeehl.elevators.models.ElevatorSearchResult;
import com.lkeehl.elevators.models.ElevatorType;

public class NoEffect extends ElevatorEffect {

	@Override
	public void playEffect(ElevatorSearchResult teleportResult, ElevatorType elevatorType, byte direction) {

	}
}
