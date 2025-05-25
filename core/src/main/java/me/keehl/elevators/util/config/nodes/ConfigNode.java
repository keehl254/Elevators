package me.keehl.elevators.util.config.nodes;

import me.keehl.elevators.util.config.ConfigConverter;

import java.util.ArrayList;

public interface ConfigNode<T> {

    String getKey();

    T getValue();

    ArrayList<ConfigNode<?>> getChildren();

    void addComment(String comment);

    ConfigConverter getConfigConverter();

    String getPath();

    ConfigRootNode<?> getRoot();

    void addChildNode(ConfigNode<?> child);

    String getChildPath(String key);

    default ConfigNode<?> getChildWithKey(String key) {
        return this.getChildren().stream().filter(i -> i.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

}
