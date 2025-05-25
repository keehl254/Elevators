package me.keehl.elevators.util.config;

public interface Config {

    default void onSave() {
    }

    default void onLoad() {
    }

    default void setKey(String key) {
    }

}
