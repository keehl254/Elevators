package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
    public void execute(ElevatorEventData eventData, Player player) {
        Consumer<ShulkerBox> soundConsumer = box -> box.getWorld().playSound(box.getLocation(), this.getGroupingObject(soundGrouping), this.getGroupingObject(volumeGrouping), this.getGroupingObject(pitchGrouping));
        ExecutionMode.executeConsumerWithMode(ConfigService.getRootConfig().effectDestination, i-> i == ExecutionMode.DESTINATION ? eventData.getDestination() : eventData.getOrigin(), soundConsumer);
    }

    @Override
    public CompletableFuture<Boolean> openCreate(ElevatorType elevatorType, Player player, byte direction) {
        return null;
    }
}
