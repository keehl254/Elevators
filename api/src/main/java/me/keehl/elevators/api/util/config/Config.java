package me.keehl.elevators.api.util.config;

public interface Config {

    default void onSave() {
    }

    default void onLoad() {
    }

    default void setKey(String key) {
    }

}
