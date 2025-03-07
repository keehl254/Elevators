package com.lkeehl.elevators.models;

import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.models.settings.ElevatorSetting;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ElevatorAction {

    // A regex pattern used to recognize the following format: key: value
    static Pattern subPattern = Pattern.compile("([a-zA-Z]+)=(.*?(?= [a-zA-Z]+=)|.*\\S)");

    private static final ElevatorActionGrouping<UUID> keyGrouping = new ElevatorActionGrouping<>(null, UUID::fromString, "identifier", "identifier", "i");

    private final ElevatorType elevatorType;

    protected String value;

    private final String key;

    private final String defaultGroupingAlias;

    private final Map<ElevatorActionGrouping<?>, Object> groupingData = new HashMap<>();

    private final List<ElevatorActionGrouping<?>> groupings;

    private final Map<ElevatorActionGrouping<?>, ElevatorSetting<?>> settings = new HashMap<>();


    protected ElevatorAction(ElevatorType elevatorType, String key, String defaultGroupingAlias, ElevatorActionGrouping<?>... groupings) {
        this.elevatorType = elevatorType;
        this.key = key;
        this.defaultGroupingAlias = defaultGroupingAlias;

        this.groupings = new ArrayList<>(Arrays.asList(groupings));
        this.groupings.add(keyGrouping);
    }

    public final void initialize(String value) {
        if (value.contains(":"))
            value = value.substring(value.indexOf(':') + 1);
        value = value.trim();
        this.value = value;

        boolean defaultGroupingSet = false;

        Matcher matcher = subPattern.matcher(this.value);
        while (matcher.find()) {
            String alias = matcher.group(1);
            if (this.calculateGroupingFromAlias(alias, matcher.group(2)))
                defaultGroupingSet = true;

            value = value.replace(this.value.substring(matcher.start(), matcher.end()), "");
        }

        if (!defaultGroupingSet)
            this.calculateGroupingFromAlias(this.defaultGroupingAlias, value);
        this.onInitialize(this.value);
    }

    public ElevatorType getElevatorType() {
        return this.elevatorType;
    }

    public String getKey() {
        return this.key;
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder(this.key + ": ");

        for (ElevatorActionGrouping<?> grouping : this.groupingData.keySet()) {
            Object value = this.groupingData.get(grouping);
            builder.append(grouping.getMainAlias());
            builder.append("=");
            builder.append(grouping.getStringFromObject(value));
            builder.append(" ");
        }

        return builder.toString().trim();
    }

    public <T> T getGroupingObject(ElevatorActionGrouping<T> grouping) {
        return this.getGroupingObject(grouping, null);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getGroupingObject(ElevatorActionGrouping<T> grouping, Elevator elevator) {

        if (elevator != null && this.settings.containsKey(grouping)) {
            ElevatorSetting<T> data = (ElevatorSetting<T>) this.settings.get(grouping);
            if (data.canBeEditedIndividually(elevator))
                return data.getIndividualElevatorValue(elevator);
        }

        if (this.groupingData.containsKey(grouping))
            return (T) this.groupingData.get(grouping);
        return grouping.getDefaultObject();
    }

    public <T> void setGroupingObject(ElevatorActionGrouping<T> grouping, T value) {
        if (value.equals(grouping.getDefaultObject()))
            this.groupingData.remove(grouping);
        else
            this.groupingData.put(grouping, value);
    }

    private boolean calculateGroupingFromAlias(String groupingAlias, String groupingValue) {
        String groupingAliasFixed = groupingAlias.trim().toLowerCase();
        String groupingValueFixed = groupingValue.trim();

        Optional<ElevatorActionGrouping<?>> grouping = this.groupings.stream().filter(i -> i.isGroupingAlias(groupingAliasFixed)).findFirst();
        grouping.ifPresent(elevatorActionGrouping -> this.groupingData.put(elevatorActionGrouping, elevatorActionGrouping.getObjectFromString(groupingValueFixed, this)));

        return grouping.map(elevatorActionGrouping -> elevatorActionGrouping.getMainAlias().equalsIgnoreCase(this.defaultGroupingAlias)).orElse(false);
    }

    protected <T> ElevatorActionSetting<T> mapSetting(ElevatorActionGrouping<T> grouping, String settingName, String description, Material icon, ChatColor textColor) {
        ElevatorActionSetting<T> setting = new ElevatorActionSetting<>(this, grouping, settingName, description, icon, textColor);
        this.settings.put(grouping, setting);

        this.initIdentifier();
        return setting;
    }

    public UUID getIdentifier() {
        return this.getGroupingObject(keyGrouping);
    }

    public void initIdentifier() {
        UUID currentIdent = this.getGroupingObject(keyGrouping);
        if (currentIdent != null) return;

        this.setGroupingObject(keyGrouping, UUID.randomUUID());
    }

    protected abstract void onInitialize(String value);

    public abstract void execute(ElevatorEventData eventData, Player player);


}
