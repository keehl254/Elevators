package me.keehl.elevators.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.helpers.TagHelper;
import me.keehl.elevators.models.*;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.interaction.PagedDisplay;
import me.keehl.elevators.util.ExecutionMode;
import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SoundAction extends ElevatorAction {

    private static final ElevatorActionGrouping<Sound> soundGrouping = new ElevatorActionGrouping<>(Sound.ENTITY_BLAZE_SHOOT, SoundAction::getSoundFromString , "sound", "s");
    private static final ElevatorActionGrouping<Float> volumeGrouping = new ElevatorActionGrouping<>(1.0F, Float::parseFloat, "volume","vol","v");
    private static final ElevatorActionGrouping<Float> pitchGrouping = new ElevatorActionGrouping<>(1.0F, Float::parseFloat, "pitch","p");
    private static final ElevatorActionGrouping<Boolean> globalGrouping = new ElevatorActionGrouping<>(true, Boolean::parseBoolean, "global","g","worldsounds","ws","w");

    public SoundAction(ElevatorType elevatorType) {
        super(elevatorType, "sound", "sound", soundGrouping, volumeGrouping, pitchGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the sound effect that plays upon elevator use.";
        ElevatorActionSetting<Sound> soundSetting = this.mapSetting(soundGrouping, "sound","Elevator Sound", desc, Material.MUSIC_DISC_CAT, ChatColor.GOLD);
        soundSetting.setupDataStore("sound-sound", PersistentDataType.STRING);
        soundSetting.onClick(this::editSound);

        desc = "This option controls whether the elevator sound is only played to the elevator user or to everyone nearby.";
        ElevatorActionSetting<Boolean> globalSetting = this.mapSetting(globalGrouping, "global","Elevator Global Sounds", desc, Material.MUSIC_DISC_CHIRP, ChatColor.BLUE);
        globalSetting.setupDataStore("sound-global", PersistentDataType.STRING);
        globalSetting.onClick(this::editGlobal);

        desc = "This option controls the volume at which the elevator sound effect plays.";
        ElevatorActionSetting<Float> volumeSetting = this.mapSetting(volumeGrouping, "volume","Elevator Volume", desc, Material.MUSIC_DISC_13, ChatColor.LIGHT_PURPLE);
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
        Consumer<Elevator> soundConsumer = elevator -> {
            ShulkerBox box = elevator.getShulkerBox();

            Sound sound = this.getGroupingObject(soundGrouping, eventData.getOrigin());
            float volume = this.getGroupingObject(volumeGrouping, eventData.getOrigin());
            float pitch = this.getGroupingObject(pitchGrouping, eventData.getOrigin());

            if(this.getGroupingObject(globalGrouping, eventData.getOrigin()))
                player.playSound(box.getLocation(), sound, volume, pitch);
            else
                box.getWorld().playSound(box.getLocation(), sound, volume, pitch);

        };
        ExecutionMode.executeConsumerWithMode(ElevatorConfigService.getRootConfig().effectDestination, eventData::getElevatorFromExecutionMode, soundConsumer);
    }

    private void editGlobal(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, boolean currentValue, Consumer<Boolean> setValueMethod) {
        setValueMethod.accept(!currentValue);
        returnMethod.run();
    }

    private void editVolume(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, float currentValue, Consumer<Float> setValueMethod) {
        float newValue = currentValue * 10.0F;
        newValue += newValue % 1;
        newValue = Math.round(newValue + (clickEvent.isLeftClick() ? 1 : -1)) / 10.0F;
        setValueMethod.accept(Math.min(Math.max(newValue, 0), 5F));
        returnMethod.run();
    }

    private void editPitch(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, float currentValue, Consumer<Float> setValueMethod) {
        float newValue = currentValue * 10.0F;
        newValue += newValue % 1;
        newValue = Math.round(newValue + (clickEvent.isLeftClick() ? 1 : -1)) / 10.0F;
        setValueMethod.accept(Math.min(Math.max(newValue, 0), 2F));
        returnMethod.run();
    }

    private void editSound(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, Sound currentValue, Consumer<Sound> setValueMethod) {
        List<Sound> sounds = Arrays.stream(Sound.values()).sorted(Comparator.comparing(Object::toString)).collect(Collectors.toList());

        PagedDisplay<Sound> display = new PagedDisplay<>(Elevators.getInstance(), player, sounds, "Actions > Settings > Sound", returnMethod);
        List<Material> materials = new ArrayList<>(TagHelper.ITEMS_CREEPER_DROP_MUSIC_DISCS.getValues());
        display.onCreateItem(sound -> {
            int hashCode = Math.abs(sound.hashCode());
            Material disc = materials.get((hashCode % materials.size()));
            ChatColor color = ChatColor.getByChar(Integer.toHexString(hashCode % 16));
            if(color == ChatColor.BLACK)
                color = ChatColor.GOLD;

            return ItemStackHelper.createItem(color + "" + ChatColor.BOLD + MessageHelper.fixEnum(sound.toString()), disc, 1);
        });
        display.onClick((item, event, myDisplay) -> {
            setValueMethod.accept(item);
            myDisplay.returnOrClose();
        });
        display.open();

    }

    private static Sound getSoundFromString(String soundKey) {
        soundKey = soundKey.toUpperCase();

        Class<Sound> clazz = Sound.class;
        if(clazz.isEnum()) {
            return Sound.valueOf(soundKey);
        } else {
            try {
                Method valueOfMethod = clazz.getDeclaredMethod("valueOf", String.class);
                valueOfMethod.setAccessible(true);
                return (Sound) valueOfMethod.invoke(null, soundKey);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignore) {
            }
        }
        return null;
    }

}
