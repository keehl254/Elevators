package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.models.*;
import com.lkeehl.elevators.models.settings.ElevatorSetting;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SoundAction extends ElevatorAction {

    private static final ElevatorActionGrouping<Sound> soundGrouping = new ElevatorActionGrouping<>(Sound.ENTITY_BLAZE_SHOOT, Sound::valueOf, "sound", "s");
    private static final ElevatorActionGrouping<Float> volumeGrouping = new ElevatorActionGrouping<>(1.0F, Float::parseFloat, "volume","vol","v");
    private static final ElevatorActionGrouping<Float> pitchGrouping = new ElevatorActionGrouping<>(1.0F, Float::parseFloat, "pitch","p");

    public SoundAction(ElevatorType elevatorType) {
        super(elevatorType, "sound", "sound", soundGrouping, volumeGrouping, pitchGrouping);

        String desc = "This option controls the sound effect that plays upon elevator use.";
        this.mapSetting(soundGrouping, "Elevator Sound", desc, Material.MUSIC_DISC_CAT, ChatColor.GOLD).onClick(this::editSound);

        desc = "This option controls the volume at which the elevator sound effect plays.";
        ElevatorActionSetting<Float> setting = this.mapSetting(volumeGrouping, "Elevator Volume", desc, Material.MUSIC_DISC_5, ChatColor.LIGHT_PURPLE, true);
        setting.setupDataStore("sound-volume", PersistentDataType.FLOAT);
        setting.onClick(this::editVolume);

        desc = "This option controls the pitch at which the elevator sound effect plays.";
        setting = this.mapSetting(pitchGrouping, "Elevator Pitch", desc, Material.MUSIC_DISC_11, ChatColor.DARK_PURPLE, true);
        setting.setupDataStore("sound-pitch", PersistentDataType.FLOAT);
        setting.onClick(this::editPitch);
    }

    @Override
    protected void onInitialize(String value) {
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        Consumer<ShulkerBox> soundConsumer = box -> box.getWorld().playSound(box.getLocation(), this.getGroupingObject(soundGrouping), this.getGroupingObject(volumeGrouping), this.getGroupingObject(pitchGrouping));
        ExecutionMode.executeConsumerWithMode(ConfigService.getRootConfig().effectDestination, i-> i == ExecutionMode.DESTINATION ? eventData.getDestination().getShulkerBox() : eventData.getOrigin().getShulkerBox(), soundConsumer);
    }

    private void editVolume(Player player, Runnable returnMethod, Float currentValue, Consumer<Float> setValueMethod) {
        returnMethod.run();
    }

    private void editPitch(Player player, Runnable returnMethod, Float currentValue, Consumer<Float> setValueMethod) {
        returnMethod.run();
    }

    private void editSound(Player player, Runnable returnMethod, Sound currentValue, Consumer<Sound> setValueMethod) {
        returnMethod.run();
    }

}
