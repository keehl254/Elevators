package me.keehl.elevators.models.settings;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorSetting;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.settings.IBuilderElevatorSetting;
import me.keehl.elevators.api.models.settings.IElevatorSettingClickContext;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.actions.ElevatorSettingClickContext;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class BuilderElevatorSetting<T> extends ElevatorSetting<T> implements IBuilderElevatorSetting<T> {

        private final T defaultValue;

        private final Function<IElevator, Boolean> canEditIndividuallyFunc;
        private final Consumer<IElevatorSettingClickContext<T>> onClick;

        public BuilderElevatorSetting(JavaPlugin plugin, @Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingName, ItemStack icon, ElevatorSettingBuilder<T> builder) {
            super(plugin,settingName, icon);

            this.comments.addAll(builder.comments);

            PersistentDataType<?, T> persistentDataType = builder.persistentDataType;
            this.defaultValue = builder.defaultValue;
            this.canEditIndividuallyFunc = builder.canEditIndividuallyFunc;

            this.setupDataStore(this.settingName, persistentDataType);

            this.onClick = builder.onClick;

            for(String action : builder.actions.keySet()) {
                super.addAction(action, builder.actions.get(action));
            }
        }

        @Override()
        public boolean canBeEditedIndividually(@NotNull IElevator elevator) {
            Objects.requireNonNull(elevator);

            return this.canEditIndividuallyFunc.apply(elevator) && !elevator.getSnapshotElevatorType().getDisabledSettings().contains(this.settingName);
        }

        @Override()
        protected final IElevatorSetting<T> setupDataStore(String settingKey, PersistentDataType<?, T> dataType) {
            super.setupDataStore(settingKey, dataType);
            return this;
        }

        @Override
        public void onClickGlobal(@NotNull Player player, @NotNull IElevatorType apiElevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull T currentValue) {
            Objects.requireNonNull(player);
            Objects.requireNonNull(apiElevatorType);
            Objects.requireNonNull(returnMethod);
            Objects.requireNonNull(clickEvent);
            Objects.requireNonNull(currentValue);

            ElevatorType elevatorType = (ElevatorType) apiElevatorType;
            ElevatorSettingClickContext<T> clickContext = new ElevatorSettingClickContext<>(player, returnMethod, clickEvent, currentValue, newValue -> {
                elevatorType.getSettingsConfig().setData(this.settingName, newValue, this.comments);
                Elevators.getInstance().saveConfig();
            });
            this.onClick.accept(clickContext);
        }

        @Override
        public void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull T currentValue) {
            Objects.requireNonNull(player);
            Objects.requireNonNull(elevator);
            Objects.requireNonNull(returnMethod);
            Objects.requireNonNull(clickEvent);
            Objects.requireNonNull(currentValue);

            ElevatorSettingClickContext<T> clickContext = new ElevatorSettingClickContext<>(player, returnMethod, clickEvent, currentValue, newValue -> {
                this.setIndividualValue(elevator, newValue);
            });
            this.onClick.accept(clickContext);
        }

        @Override
        public @NotNull T getGlobalValue(@NotNull IElevatorType apiElevatorType) {
            Objects.requireNonNull(apiElevatorType);

            ElevatorType elevatorType = (ElevatorType) apiElevatorType;
            T currentValue = elevatorType.getSettingsConfig().getData(this.settingName);
            if (currentValue == null)
                return this.defaultValue;
            return currentValue;
        }

        @Override()
        public @NotNull IElevatorSetting<T> addAction(@NotNull String action, @NotNull String description) {
            Objects.requireNonNull(action);
            Objects.requireNonNull(description);

            throw new RuntimeException("addAction func cannot be dynamically set on external elevator settings.");
        }

    }