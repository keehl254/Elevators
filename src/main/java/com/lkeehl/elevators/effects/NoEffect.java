package com.lkeehl.elevators.effects;

import com.lkeehl.elevators.models.ElevatorEffect;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.util.ExecutionMode;

public class NoEffect extends ElevatorEffect {

	public NoEffect() {
		super("NONE");
	}
	@Override
	public void playEffect(ElevatorEventData teleportResult, ExecutionMode executionMode) {

	}
}
