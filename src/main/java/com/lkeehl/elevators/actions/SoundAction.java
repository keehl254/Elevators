package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.*;
import com.lkeehl.elevators.models.settings.ElevatorSetting;
import com.lkeehl.elevators.services.ConfigService;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

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
        String desc = "This option controls the sound effect that plays upon elevator use.";
        ElevatorActionSetting<Sound> soundSetting = this.mapSetting(soundGrouping, "sound","Elevator Sound", desc, Material.MUSIC_DISC_CAT, ChatColor.GOLD);
        soundSetting.onClick(this::editSound);

        desc = "This option controls the volume at which the elevator sound effect plays.";
        ElevatorActionSetting<Float> volumeSetting = this.mapSetting(volumeGrouping, "volume","Elevator Volume", desc, Material.MUSIC_DISC_5, ChatColor.LIGHT_PURPLE);
        volumeSetting.setupDataStore("sound-volume", PersistentDataType.STRING);
        volumeSetting.onClick(this::editVolume);
        volumeSetting.addAction("Left Click", "Raise Volume");
        volumeSetting.addAction("Right Click", "Lower Volume");

        desc = "This option controls the pitch at which the elevator sound effect plays.";
        ElevatorActionSetting<Float> pitchSetting = this.mapSetting(pitchGrouping, "pitch","Elevator Pitch", desc, Material.MUSIC_DISC_11, ChatColor.DARK_PURPLE);
        pitchSetting.setupDataStore("sound-pitch", PersistentDataType.STRING);
        pitchSetting.onClick(this::editPitch);
        pitchSetting.addAction("Left Click", "Raise Pitch");
        pitchSetting.addAction("Right Click", "Lower Pitch");
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        Consumer<ShulkerBox> soundConsumer = box -> box.getWorld().playSound(box.getLocation(), this.getGroupingObject(soundGrouping, eventData.getOrigin()), this.getGroupingObject(volumeGrouping, eventData.getOrigin()), this.getGroupingObject(pitchGrouping, eventData.getOrigin()));
        ExecutionMode.executeConsumerWithMode(ConfigService.getRootConfig().effectDestination, i-> i == ExecutionMode.DESTINATION ? eventData.getDestination().getShulkerBox() : eventData.getOrigin().getShulkerBox(), soundConsumer);
    }

    private void editVolume(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, Float currentValue, Consumer<Float> setValueMethod) {
        float newValue = currentValue * 10.0F;
        newValue += newValue % 1;
        newValue = Math.round(newValue + (clickEvent.isLeftClick() ? 1 : -1)) / 10.0F;
        setValueMethod.accept(Math.clamp(newValue, 0, 5F));
        returnMethod.run();
    }

    private void editPitch(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, Float currentValue, Consumer<Float> setValueMethod) {
        float newValue = currentValue * 10.0F;
        newValue += newValue % 1;
        newValue = Math.round(newValue + (clickEvent.isLeftClick() ? 1 : -1)) / 10.0F;
        setValueMethod.accept(Math.clamp(newValue, 0, 2F));
        returnMethod.run();
    }

    private void editSound(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, Sound currentValue, Consumer<Sound> setValueMethod) {
        returnMethod.run();
    }

}
