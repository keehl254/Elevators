package me.keehl.elevators.models.settings;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.settings.IElevatorSettingBuilder;
import me.keehl.elevators.api.models.settings.IElevatorSettingClickContext;
import me.keehl.elevators.api.services.IElevatorSettingService;
import me.keehl.elevators.api.util.persistantDataTypes.ElevatorsDataType;
import me.keehl.elevators.helpers.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ElevatorSettingBuilder<T> implements IElevatorSettingBuilder<T> {

    protected final String settingKey;

    protected final T defaultValue;
    protected final PersistentDataType<?, T> persistentDataType;

    protected Consumer<IElevatorSettingClickContext<T>> onClick = IElevatorSettingClickContext::close;

    protected Function<IElevator, Boolean> canEditIndividuallyFunc;
    protected final Map<String, String> actions = new HashMap<>();
    protected final List<String> comments = new ArrayList<>();

    public ElevatorSettingBuilder(String settingKey, T defaultValue, PersistentDataType<?, T> persistentDataType) {
        this.settingKey = settingKey;
        this.defaultValue = defaultValue;
        this.persistentDataType = persistentDataType;
    }

    public ElevatorSettingBuilder(String settingKey, T defaultValue, ElevatorsDataType elevatorsDataType) {
        this(settingKey, defaultValue, elevatorsDataType.getDataType());
    }

    public BuilderElevatorSetting<T> register(JavaPlugin plugin, ItemStack icon) {
        BuilderElevatorSetting<T> setting = new BuilderElevatorSetting<>(plugin, this.settingKey, icon, this);

        IElevatorSettingService service = Optional.ofNullable(Bukkit.getServicesManager().load(IElevatorSettingService.class)).orElseThrow();
        service.addSetting(setting);
        return setting;
    }

    public void register(JavaPlugin plugin, String chatColor, String displayName, Material itemType) {
        this.register(plugin, ItemStackHelper.createItem(chatColor + ChatColor.BOLD + displayName, itemType, 1));
    }

    public ElevatorSettingBuilder<T> addAction(String trigger, String action) {
        this.actions.put(trigger, action);
        return this;
    }

    public ElevatorSettingBuilder<T> addComment(String comment) {
        this.comments.add(comment);
        return this;
    }

    public ElevatorSettingBuilder<T> setCanEditIndividually(Function<IElevator, Boolean> canEditFunc) {
        this.canEditIndividuallyFunc = canEditFunc;
        return this;
    }

    public ElevatorSettingBuilder<T> allowPerEleCustomization() {
        this.canEditIndividuallyFunc = (elevator) -> true;
        return this;
    }

    public ElevatorSettingBuilder<T> onClick(Consumer<IElevatorSettingClickContext<T>> onClick) {
        this.onClick = onClick;
        return this;
    }

}
