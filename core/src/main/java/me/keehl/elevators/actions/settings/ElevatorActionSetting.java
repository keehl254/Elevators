package me.keehl.elevators.actions.settings;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorAction;
import me.keehl.elevators.models.ElevatorActionVariable;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.ElevatorSetting;
import me.keehl.elevators.util.PentaConsumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Subst;

import java.util.function.Consumer;

public class ElevatorActionSetting<T> extends ElevatorSetting<String> {

    private final ElevatorAction elevatorAction;
    private final ElevatorActionVariable<T> actionGrouping;

    private final boolean useDataStore;

    private PentaConsumer<Player, Runnable, InventoryClickEvent, T, Consumer<T>> onClickMethod;

    public ElevatorActionSetting(JavaPlugin plugin, ElevatorAction action, ElevatorActionVariable<T> grouping, @Subst("test_key") String settingName, String settingDisplayName, String description, Material icon, boolean useDataStore) {
        super(plugin,action.getKey()+"/"+settingName, settingDisplayName, description, icon);

        this.elevatorAction = action;
        this.actionGrouping = grouping;
        this.useDataStore = useDataStore;

        if(useDataStore)
            this.setupDataStore(settingName, PersistentDataType.STRING);
    }

    public void onClick(PentaConsumer<Player, Runnable, InventoryClickEvent, T, Consumer<T>> setValueGlobalMethod) {
        this.onClickMethod = setValueGlobalMethod;
    }

    @Override()
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue) {
        this.onClickMethod.accept(player, returnMethod, clickEvent, this.actionGrouping.getObjectFromString(currentValue, this.elevatorAction), val -> this.elevatorAction.setGroupingObject(this.actionGrouping, val));
    }

    @Override()
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue) {
        this.elevatorAction.initIdentifier();
        this.onClickMethod.accept(player, returnMethod, clickEvent, this.actionGrouping.getObjectFromString(currentValue, this.elevatorAction), val -> this.setIndividualValue(elevator, this.actionGrouping.getStringFromObject(val)));
    }

    @Override
    public String getGlobalValue(ElevatorType elevatorType) {
        return this.actionGrouping.getStringFromObject(this.elevatorAction.getVariableValue(this.actionGrouping));
    }

    @Override()
    public ElevatorSetting<String> setupDataStore(String settingKey, PersistentDataType<?, String> dataType) {
        if (this.elevatorAction.getIdentifier() != null) // Should never be null as mapSetting of ElevatorAction initializes the value.
            settingKey = this.elevatorAction.getIdentifier() + "-" + settingKey;
        return super.setupDataStore(settingKey, dataType);
    }

    @Override
    public boolean canBeEditedIndividually(Elevator elevator) {
        return this.useDataStore;
    }


}
