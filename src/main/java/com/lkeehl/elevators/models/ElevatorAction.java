package com.lkeehl.elevators.models;

import com.lkeehl.elevators.models.ElevatorActionGrouping;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ElevatorAction {

    // A regex pattern used to recognize the following format: key: value
    static Pattern subPattern = Pattern.compile("([a-zA-Z]+)=(.*?(?= [a-zA-Z]+=)|.*\\S)");

    private final ElevatorType elevatorType;

    protected String value;

    private final String key;

    private final String defaultGroupingAlias;

    private final Map<ElevatorActionGrouping<?>, Object> groupingData = new HashMap<>();

    private final List<ElevatorActionGrouping<?>> groupings;


    protected ElevatorAction(ElevatorType elevatorType, String key, String defaultGroupingAlias, ElevatorActionGrouping<?>... groupings) {
        this.elevatorType = elevatorType;
        this.key = key;
        this.defaultGroupingAlias = defaultGroupingAlias;

        this.groupings = Arrays.asList(groupings);
    }

    public final void initialize(String value) {
        if(value.contains(":"))
            value = value.substring(value.indexOf(':') + 1);
        this.value = value.trim();

        Matcher matcher = subPattern.matcher(this.value);
        while (matcher.find()) {
            this.calculateGroupingFromAlias(matcher.group(1), matcher.group(2));
            value = value.replace(value.substring(matcher.start(), matcher.end()), "");
        }

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
        StringBuilder builder = new StringBuilder(this.key+": ");

        for(ElevatorActionGrouping<?> grouping : this.groupingData.keySet()) {
            Object value = this.groupingData.get(grouping);
            builder.append(grouping.getMainAlias());
            builder.append("=");
            builder.append(grouping.getStringFromObject(value));
            builder.append(" ");
        }

        return builder.toString().trim();
    }

    @SuppressWarnings("unchecked")
    protected <T> T getGroupingObject(ElevatorActionGrouping<T> grouping) {
        if(this.groupingData.containsKey(grouping))
            return (T) this.groupingData.get(grouping);
        return grouping.getDefaultObject();
    }

    private void calculateGroupingFromAlias(String groupingAlias, String groupingValue) {
        String groupingAliasFixed = groupingAlias.trim().toLowerCase();
        String groupingValueFixed = groupingValue.trim();

        Optional<ElevatorActionGrouping<?>> grouping = this.groupings.stream().filter(i -> i.isGroupingAlias(groupingAliasFixed)).findFirst();
        grouping.ifPresent(elevatorActionGrouping -> groupingData.put(elevatorActionGrouping, elevatorActionGrouping.getObjectFromString(groupingValueFixed, this)));
    }

    protected abstract void onInitialize(String value);

    public abstract void execute(ShulkerBox origin, ShulkerBox destination, ElevatorType elevatorType, Player player);

    public abstract CompletableFuture<Boolean> openCreate(ElevatorType elevator, Player player, byte direction);

}
