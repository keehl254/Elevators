package me.keehl.elevators.api.services.interaction;

import java.util.Arrays;
import java.util.stream.Stream;

public enum DisplayClickResult {
        CANCEL,
        ALLOW,
        DEFAULT;

        public static DisplayClickResult combineResults(DisplayClickResult defaultClickResult, DisplayClickResult... results) {
            Stream<DisplayClickResult> resultStream = Arrays.stream(results).filter(i -> i != DisplayClickResult.DEFAULT);
            return resultStream.filter(i -> i != defaultClickResult).findAny().orElse(defaultClickResult);
        }
    }