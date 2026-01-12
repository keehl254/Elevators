package me.keehl.elevators.api.util.config;

import me.keehl.elevators.api.util.config.nodes.ConfigNode;

public interface Config {

    default void onSave() {
    }

    default void onLoad() {
    }

    default void setKey(String key) {
    }

    default void setNode(ConfigNode<?> key) {
    }

}
