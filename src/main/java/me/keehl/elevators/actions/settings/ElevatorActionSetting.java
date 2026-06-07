package me.keehl.elevators.actions.settings;

import me.keehl.elevators.api.models.*;
import me.keehl.elevators.models.settings.ElevatorSetting;
import me.keehl.elevators.api.util.PentaConsumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class ElevatorActionSetting<T> extends ElevatorSetting<String> implements IElevatorActionSetting<T> {

    private final IElevatorAction elevatorAction;
    private final IElevatorActionVariable<T> actionGrouping;

    private final boolean useDataStore;

    private PentaConsumer<Player, Runnable, InventoryClickEvent, T, Consumer<T>> onClickMethod;

    public ElevatorActionSetting(JavaPlugin plugin, IElevatorAction action, IElevatorActionVariable<T> grouping, @Subst("test_key") String settingName, String settingDisplayName, String description, Material icon, boolean useDataStore) {
        super(plugin,action.getKey()+"/"+settingName, settingDisplayName, description, icon);

        this.elevatorAction = action;
        this.actionGrouping = grouping;
        this.useDataStore = useDataStore;

        if(useDataStore)
            this.setupDataStore(settingName, PersistentDataType.STRING);
    }

    public void onClick(@NotNull PentaConsumer<Player, Runnable, InventoryClickEvent, T, Consumer<T>> setValueGlobalMethod) {
        Objects.requireNonNull(setValueGlobalMethod);
        this.onClickMethod = setValueGlobalMethod;
    }

    @Override()
    public void onClickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull String currentValue) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(elevatorType);
        Objects.requireNonNull(returnMethod);
        Objects.requireNonNull(clickEvent);
        Objects.requireNonNull(currentValue);
        if (this.onClickMethod == null)
            throw new IllegalStateException("No onClick handler registered for setting '" + this.settingName + "'.");
        this.onClickMethod.accept(player, returnMethod, clickEvent, this.actionGrouping.getObjectFromString(currentValue, this.elevatorAction), val -> this.elevatorAction.setGroupingObject(this.actionGrouping, val));
    }

    @Override()
    public void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull String currentValue) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(elevator);
        Objects.requireNonNull(returnMethod);
        Objects.requireNonNull(clickEvent);
        Objects.requireNonNull(currentValue);

        if (this.onClickMethod == null)
            throw new IllegalStateException("No onClick handler registered for setting '" + this.settingName + "'.");
        this.elevatorAction.initIdentifier();
        this.onClickMethod.accept(player, returnMethod, clickEvent, this.actionGrouping.getObjectFromString(currentValue, this.elevatorAction), val -> this.setIndividualValue(elevator, this.actionGrouping.getStringFromObject(val)));
    }

    @Override
    public @NotNull String getGlobalValue(@NotNull IElevatorType elevatorType) {
        Objects.requireNonNull(elevatorType, "elevatorType");
        return this.actionGrouping.getStringFromObject(this.elevatorAction.getVariableValue(this.actionGrouping));
    }

    @Override()
    public @NotNull IElevatorSetting<String> setupDataStore(@NotNull String settingKey, @NotNull PersistentDataType<?, String> dataType) {
        Objects.requireNonNull(settingKey, "settingKey");
        Objects.requireNonNull(dataType, "dataType");

        this.elevatorAction.initIdentifier(); // Just in case.
        settingKey = this.elevatorAction.getIdentifier() + "-" + settingKey;
        return super.setupDataStore(settingKey, dataType);
    }

    @Override
    public boolean canBeEditedIndividually(@NotNull IElevator elevator) {
        return this.useDataStore;
    }


}
