package me.keehl.elevators.api.models;

import me.keehl.elevators.api.services.interaction.ISimpleDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Subst;

import java.util.*;
import java.util.regex.Pattern;

public interface IElevatorAction {


    // A regex pattern used to recognize the following format: key: value
    Pattern subPattern = Pattern.compile("([a-zA-Z]+)=(.*?(?= [a-zA-Z]+=)|.*\\S)");

    void setIcon(ItemStack item);

    void initialize(String value);

    IElevatorType getElevatorType();

    @Subst("test_key")
    String getKey();

    ItemStack getIcon();

    String serialize();

    <T> T getVariableValue(IElevatorActionVariable<T> grouping);

    <T> T getVariableValue(IElevatorActionVariable<T> variable, IElevator elevator);

    <T> void setGroupingObject(IElevatorActionVariable<T> grouping, T value);

    UUID getIdentifier();

    List<IElevatorActionSetting<?>> getSettings();

    void initIdentifier();

    void onStartEditing(Player player, ISimpleDisplay display, IElevator elevator);
    void onStopEditing(Player player, ISimpleDisplay display, IElevator elevator);

    boolean meetsConditions(IElevatorEventData eventData, Player player);

    void execute(IElevatorEventData eventData, Player player);




}
