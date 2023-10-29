package com.lkeehl.elevators.util;

import com.lkeehl.elevators.services.ConfigService;

import java.util.function.Consumer;
import java.util.function.Function;

public enum ExecutionMode {

    BOTH,
    ORIGIN,
    DESTINATION;

    public static <T> void executeConsumerWithMode(Function<ExecutionMode, T> modeConverter, Consumer<T> execConsumer) {
        switch (ConfigService.getRootConfig().effectDestination) {
            case BOTH:
                execConsumer.accept(modeConverter.apply(ExecutionMode.ORIGIN));
            case DESTINATION:
                execConsumer.accept(modeConverter.apply(ExecutionMode.DESTINATION));
                break;
            case ORIGIN:
                execConsumer.accept(modeConverter.apply(ExecutionMode.ORIGIN));
        }
    }

}
