package me.keehl.elevators.api.util.config.nodes;

import me.keehl.elevators.api.util.config.converter.IConfigConverter;

import java.util.ArrayList;
import java.util.List;

public interface ConfigNode<T> {

    String getKey();

    T getValue();

    ConfigNode<?> getParent();

    ArrayList<ConfigNode<?>> getChildren();

    void addComment(String comment);

    List<String> getComments();

    void clearComments();

    IConfigConverter getConfigConverter();

    String getPath();

    IConfigRootNode<?> getRoot();

    void addChildNode(ConfigNode<?> child);

    String getChildPath(String key);

    default ConfigNode<?> getChildWithKey(String key) {
        return this.getChildren().stream().filter(i -> i.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
    }

}
