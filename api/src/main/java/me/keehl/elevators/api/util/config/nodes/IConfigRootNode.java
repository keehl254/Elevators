package me.keehl.elevators.api.util.config.nodes;

import me.keehl.elevators.api.util.config.Config;

import java.util.*;

public interface IConfigRootNode<T extends Config> extends ConfigNode<T> {

    Object getObjectAtPath(String path, Object defaultValue);

    T getConfig();

    Object serializeToObject();

    void addComment(String path, String comment);

    List<String> getCommentsAtPath(String path);

    void clearCommentsAtPath(String path);

}
