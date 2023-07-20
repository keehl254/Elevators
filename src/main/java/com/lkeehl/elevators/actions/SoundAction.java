package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import java.util.concurrent.CompletableFuture;

public class SoundAction extends ElevatorAction {

    private static final ElevatorActionGrouping<Sound> soundGrouping = new ElevatorActionGrouping<>(Sound.ENTITY_BLAZE_SHOOT, Sound::valueOf, "sound", "s");
    private static final ElevatorActionGrouping<Float> volumeGrouping = new ElevatorActionGrouping<>(1.0F, Float::parseFloat, "volume","vol","v");
    private static final ElevatorActionGrouping<Float> pitchGrouping = new ElevatorActionGrouping<>(1.0F, Float::parseFloat, "pitch","p");

    public SoundAction(ElevatorType elevatorType) {
        super(elevatorType, "sound", "sound", soundGrouping, volumeGrouping, pitchGrouping);
    }

    @Override
    protected void onInitialize(String value) {
    }

    @Override
    public void execute(ShulkerBox from, ShulkerBox to, ElevatorType elevator, Player player) {
        from.getWorld().playSound(from.getLocation(), this.getGroupingObject(soundGrouping), this.getGroupingObject(volumeGrouping), this.getGroupingObject(pitchGrouping));
    }

    @Override
    public CompletableFuture<Boolean> openCreate(ElevatorType elevatorType, Player player, byte direction) {
        return null;
    }
}
