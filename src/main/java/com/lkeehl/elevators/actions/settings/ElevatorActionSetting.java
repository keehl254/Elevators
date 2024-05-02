package com.lkeehl.elevators.actions.settings;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorAction;
import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.settings.ElevatorSetting;
import com.lkeehl.elevators.util.QuadConsumer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class ElevatorActionSetting<T> extends ElevatorSetting<T> {

    private final ElevatorAction elevatorAction;
    private final ElevatorActionGrouping<T> actionGrouping;

    private QuadConsumer<Player, Runnable, T, Consumer<T>> onClickMethod;

    public ElevatorActionSetting(ElevatorAction action, ElevatorActionGrouping<T> grouping, String settingName, String description, Material icon, ChatColor textColor) {
        this(action, grouping, settingName, description, icon, textColor, false);
    }

    public ElevatorActionSetting(ElevatorAction action, ElevatorActionGrouping<T> grouping, String settingName, String description, Material icon, ChatColor textColor, boolean supportsIndividualEditing) {
        super(settingName, description, icon, textColor, supportsIndividualEditing);

        this.elevatorAction = action;
        this.actionGrouping = grouping;

        this.setGetValueGlobal(et -> action.getGroupingObject(grouping));
    }

    public void onClick(QuadConsumer<Player, Runnable, T, Consumer<T>> setValueGlobalMethod) {
        this.onClickMethod = setValueGlobalMethod;
    }

    @Override()
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, T currentValue) {
        this.onClickMethod.accept(player, returnMethod, currentValue, val -> elevatorAction.setGroupingObject(this.actionGrouping, val));
    }

    @Override()
    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, T currentValue) {
        this.elevatorAction.initIdentifier();
        this.onClickMethod.accept(player, returnMethod, currentValue, val -> this.setIndividualElevatorValue(elevator, val));
    }

    @Override()
    public ElevatorSetting<T> setupDataStore(String settingKey, PersistentDataType<?, T> dataType) {
        if(this.elevatorAction.getIdentifier() != null) // Should never be null as mapSetting of ElevatorAction initializes the value.
            settingKey = this.elevatorAction.getIdentifier()+"-"+settingKey;
        return super.setupDataStore(settingKey, dataType);
    }


}
