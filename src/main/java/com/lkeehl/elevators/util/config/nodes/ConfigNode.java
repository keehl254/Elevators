package com.lkeehl.elevators.util.config.nodes;

import com.lkeehl.elevators.util.config.ConfigConverter;

import java.util.ArrayList;

public interface ConfigNode<T> {

    String getKey();

    T getValue();

    ArrayList<ConfigNode<?>> getChildren();

    void addComment(String comment);

    ConfigConverter getConfigConverter();

    String getPath();

    ConfigRootNode getRoot();

    void addChildNode(ConfigNode<?> child);

    String getChildPath(String key);

}
