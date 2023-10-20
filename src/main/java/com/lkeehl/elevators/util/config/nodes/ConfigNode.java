package com.lkeehl.elevators.util.config.nodes;

import com.lkeehl.elevators.util.config.ConfigConverter;

import java.util.List;

public interface ConfigNode<T> {

    String getKey();

    T getValue();

    List<ConfigNode<?>> getChildren();

    void addComment(String comment);

    ConfigConverter getConfigConverter();

    String getPath();

    ConfigRootNode getRoot();

    void addChildNode(ConfigNode<?> child);

    String getChildPath(String key);

}
