package com.lkeehl.elevators.effects;

import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.ElevatorEffect;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.Material;

public class NoEffect extends ElevatorEffect {

	public NoEffect() {
		super("NONE", ItemStackHelper.createItem("No Effect", Material.BARRIER, 1));
	}
	@Override
	public void playEffect(ElevatorEventData teleportResult, ExecutionMode executionMode) {

	}
}
