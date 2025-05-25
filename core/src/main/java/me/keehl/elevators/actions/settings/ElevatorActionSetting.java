package me.keehl.elevators.actions.settings;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorAction;
import me.keehl.elevators.models.ElevatorActionGrouping;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.settings.ElevatorSetting;
import me.keehl.elevators.util.PentaConsumer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class ElevatorActionSetting<T> extends ElevatorSetting<String> {

    private final ElevatorAction elevatorAction;
    private final ElevatorActionGrouping<T> actionGrouping;

    private PentaConsumer<Player, Runnable, InventoryClickEvent, T, Consumer<T>> onClickMethod;

    public ElevatorActionSetting(ElevatorAction action, ElevatorActionGrouping<T> grouping, String settingName, String settingDisplayName, String description, Material icon, ChatColor textColor) {
        super(action.getKey()+"/"+settingName, settingDisplayName, description, icon, textColor);

        this.elevatorAction = action;
        this.actionGrouping = grouping;

        this.setGetValueGlobal(et -> grouping.getStringFromObject(action.getGroupingObject(grouping)));
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
        this.onClickMethod.accept(player, returnMethod, clickEvent, this.actionGrouping.getObjectFromString(currentValue, this.elevatorAction), val -> this.setIndividualElevatorValue(elevator, this.actionGrouping.getStringFromObject(val)));
    }

    @Override()
    public ElevatorSetting<String> setupDataStore(String settingKey, PersistentDataType<?, String> dataType) {
        if (this.elevatorAction.getIdentifier() != null) // Should never be null as mapSetting of ElevatorAction initializes the value.
            settingKey = this.elevatorAction.getIdentifier() + "-" + settingKey;
        return super.setupDataStore(settingKey, dataType);
    }


}
