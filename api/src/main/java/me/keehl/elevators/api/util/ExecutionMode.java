package me.keehl.elevators.api.util;

import java.util.function.Consumer;
import java.util.function.Function;

public enum ExecutionMode {

    BOTH,
    ORIGIN,
    DESTINATION;

    public static <T> void executeConsumerWithMode(ExecutionMode mode, Function<ExecutionMode, T> modeConverter, Consumer<T> execConsumer) {
        switch (mode) {
            case BOTH:
                execConsumer.accept(modeConverter.apply(ExecutionMode.ORIGIN));
                execConsumer.accept(modeConverter.apply(ExecutionMode.DESTINATION));
            case DESTINATION:
                execConsumer.accept(modeConverter.apply(ExecutionMode.DESTINATION));
                break;
            case ORIGIN:
                execConsumer.accept(modeConverter.apply(ExecutionMode.ORIGIN));
        }
    }

}
