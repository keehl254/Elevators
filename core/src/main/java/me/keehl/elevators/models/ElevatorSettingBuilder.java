package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.services.ElevatorSettingService;
import me.keehl.elevators.util.persistantDataTypes.ElevatorsDataType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ElevatorSettingBuilder<T> {

    protected final String settingKey;

    protected final T defaultValue;
    protected final PersistentDataType<?, T> persistentDataType;

    protected Consumer<ElevatorSettingClickContext<T>> onClick = ElevatorSettingClickContext::close;

    protected Function<Elevator, Boolean> canEditIndividuallyFunc;
    protected final Map<String, String> actions = new HashMap<>();
    protected final List<String> comments = new ArrayList<>();

    public ElevatorSettingBuilder(@Pattern("[a-z0-9/._-]+") String settingKey, T defaultValue, PersistentDataType<?, T> persistentDataType) {
        this.settingKey = settingKey;
        this.defaultValue = defaultValue;
        this.persistentDataType = persistentDataType;
    }

    public ElevatorSettingBuilder(@Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingKey, T defaultValue, ElevatorsDataType elevatorsDataType) {
        this(settingKey, defaultValue, elevatorsDataType.getDataType());
    }

    public BuilderElevatorSetting<T> register(JavaPlugin plugin, ItemStack icon) {
        BuilderElevatorSetting<T> setting = new BuilderElevatorSetting<T>(plugin, this.settingKey, icon, this);
        ElevatorSettingService.addSetting(setting);

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

    public ElevatorSettingBuilder<T> setCanEditIndividually(Function<Elevator, Boolean> canEditFunc) {
        this.canEditIndividuallyFunc = canEditFunc;
        return this;
    }

    public ElevatorSettingBuilder<T> setCanEditIndividually() {
        this.canEditIndividuallyFunc = (elevator) -> true;
        return this;
    }

    public ElevatorSettingBuilder<T> setOnClick(Consumer<ElevatorSettingClickContext<T>> onClick) {
        this.onClick = onClick;
        return this;
    }

    public static class BuilderElevatorSetting<T> extends ElevatorSetting<T> {

        private final T defaultValue;

        private final Function<Elevator, Boolean> canEditIndividuallyFunc;
        private final Consumer<ElevatorSettingClickContext<T>> onClick;

        public BuilderElevatorSetting(JavaPlugin plugin, @Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingName, ItemStack icon, ElevatorSettingBuilder<T> builder) {
            super(plugin,settingName, icon);

            this.comments.addAll(builder.comments);

            PersistentDataType<?, T> persistentDataType = builder.persistentDataType;
            this.defaultValue = builder.defaultValue;
            this.canEditIndividuallyFunc = builder.canEditIndividuallyFunc;

            this.setupDataStore(this.settingName, persistentDataType);

            this.onClick = builder.onClick;

            for(String action : builder.actions.keySet())
                super.addAction(action, builder.actions.get(action));

        }

        @Override()
        public boolean canBeEditedIndividually(Elevator elevator) {
            return this.canEditIndividuallyFunc.apply(elevator) && !elevator.getElevatorType(false).getDisabledSettings().contains(this.settingName);
        }

        @Override()
        protected final ElevatorSetting<T> setupDataStore(String settingKey, PersistentDataType<?, T> dataType) {
            super.setupDataStore(settingKey, dataType);
            return this;
        }

        @Override
        public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, T currentValue) {
            ElevatorSettingClickContext<T> clickContext = new ElevatorSettingClickContext<>(player, returnMethod, clickEvent, currentValue, newValue -> {
                elevatorType.getSettingsConfig().setData(this.settingName, newValue, this.comments);
                Elevators.getInstance().saveConfig();
            });
            this.onClick.accept(clickContext);
        }

        @Override
        public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, T currentValue) {
            ElevatorSettingClickContext<T> clickContext = new ElevatorSettingClickContext<>(player, returnMethod, clickEvent, currentValue, newValue -> {
                this.setIndividualValue(elevator, newValue);
            });
            this.onClick.accept(clickContext);
        }

        @Override
        public T getGlobalValue(ElevatorType elevatorType) {
            T currentValue = elevatorType.getSettingsConfig().getData(this.settingName);
            if (currentValue == null)
                return this.defaultValue;
            return currentValue;
        }

        @Override()
        public ElevatorSetting<T> addAction(String action, String description) {
            throw new RuntimeException("addAction func cannot be dynamically set on external elevator settings.");
        }

    }

}
