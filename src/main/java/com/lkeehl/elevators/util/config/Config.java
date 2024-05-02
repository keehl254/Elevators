package com.lkeehl.elevators.util.config;

public interface Config {

    public default void onSave() {
    }

    public default void onLoad() {
    }

    public default void setKey(String key) {
    }

}
