package com.lkeehl.elevators.models;

import com.lkeehl.elevators.Elevators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ElevatorActionGrouping<T> {

    private final T defaultObject;

    private final Function<String, T> conversionFunction;
    private final Function<T, String> toStringFunction;

    private final String conversionErrorMessage;

    private final List<String> groupingAliases;

    public ElevatorActionGrouping(T defaultObject, Function<String, T> conversionFunction, String mainAlias, String... aliases) {
        this(defaultObject, conversionFunction, Objects::toString, mainAlias, aliases);
    }

    public ElevatorActionGrouping(T defaultObject, Function<String, T> conversionFunction, Function<T, String> toStringFunction, String mainAlias, String... aliases) {
        this.defaultObject = defaultObject;
        this.conversionFunction = conversionFunction;
        this.toStringFunction = toStringFunction;

        List<String> groupingAliases = new ArrayList<>();
        groupingAliases.add(mainAlias.toLowerCase());
        groupingAliases.addAll(Arrays.stream(aliases).map(String::toLowerCase).toList());

        this.groupingAliases = groupingAliases;

        this.conversionErrorMessage = "An invalid value was provided for action %s on elevator type '%s'. Defaulting to '%s'";
    }

    public T getObjectFromString(String value, ElevatorAction action) {
        try {
            return this.conversionFunction.apply(value);
        } catch (Exception e) {
            Elevators.getElevatorsLogger().warning(String.format(this.conversionErrorMessage, action.getKey(), action.getElevatorType().getTypeKey(), this.defaultObject.toString()));
            return this.defaultObject;
        }
    }

    @SuppressWarnings("unchecked")
    public String getStringFromObject(Object object) {
        try {
            return this.toStringFunction.apply((T) object);
        } catch (Exception e) {
            return object.toString();
        }
    }

    public String getMainAlias() {
        return groupingAliases.get(0);
    }

    public T getDefaultObject() {
        return this.defaultObject;
    }

    public boolean isGroupingAlias(String alias) {
        return this.groupingAliases.contains(alias.toLowerCase());
    }

}
