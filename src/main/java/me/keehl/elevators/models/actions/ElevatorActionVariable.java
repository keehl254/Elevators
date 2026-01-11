package me.keehl.elevators.models.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorActionVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;

public class ElevatorActionVariable<T> implements IElevatorActionVariable<T> {

    private final T defaultObject;

    private final Function<String, T> conversionFunction;
    private final Function<T, String> toStringFunction;

    private final String conversionErrorMessage;

    private final List<String> groupingAliases;

    public ElevatorActionVariable(T defaultObject, Function<String, T> conversionFunction, String mainAlias, String... aliases) {
        this(defaultObject, conversionFunction, Objects::toString, mainAlias, aliases);
    }

    public ElevatorActionVariable(T defaultObject, Function<String, T> conversionFunction, Function<T, String> toStringFunction, String mainAlias, String... aliases) {
        this.defaultObject = defaultObject;
        this.conversionFunction = conversionFunction;
        this.toStringFunction = toStringFunction;

        List<String> groupingAliases = new ArrayList<>();
        groupingAliases.add(mainAlias.toLowerCase());
        groupingAliases.addAll(Arrays.stream(aliases).map(String::toLowerCase).toList());

        this.groupingAliases = groupingAliases;
        this.conversionErrorMessage = "An invalid value was provided for action %s '%s' on elevator type '%s'. Defaulting to '%s'";
    }

    public T getObjectFromString(String value, IElevatorAction action) {
        if(value == null) {
            ElevatorsAPI.log("Null value. Using default");
            return this.defaultObject;
        }
        try {
            return this.conversionFunction.apply(value);
        } catch (Exception e) {
            ElevatorsAPI.log(Level.INFO, this.getMainAlias() + ": " + value);
            ElevatorsAPI.log(Level.WARNING, String.format(this.conversionErrorMessage, action.getKey(), this.getMainAlias(), action.getElevatorType().getTypeKey(), this.defaultObject.toString()));
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
        return this.groupingAliases.getFirst();
    }

    public T getDefaultObject() {
        return this.defaultObject;
    }

    public boolean isGroupingAlias(String alias) {
        return this.groupingAliases.contains(alias.toLowerCase());
    }

}
