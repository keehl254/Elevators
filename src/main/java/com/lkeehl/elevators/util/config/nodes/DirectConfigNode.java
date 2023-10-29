package com.lkeehl.elevators.util.config.nodes;

import com.lkeehl.elevators.util.config.ConfigConverter;

import java.util.ArrayList;

public class DirectConfigNode<T> implements ConfigNode<T> {

    private final ConfigNode<?> parentNode;

    private final String key;
    private final T value;

    private final ArrayList<ConfigNode<?>> children = new ArrayList<>();

    public DirectConfigNode(ConfigNode<?> parentNode, String key, T value) {
        this.parentNode = parentNode;
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public ArrayList<ConfigNode<?>> getChildren() {
        return this.children;
    }

    @Override
    public void addComment(String comment) {
        this.getRoot().addComment(getPath(), comment);
    }

    @Override
    public ConfigConverter getConfigConverter() {
        return ConfigConverter.getConverter(this.value.getClass());
    }

    @Override
    public String getPath() {
        if(parentNode instanceof ConfigRootNode)
            return this.key;
        return parentNode.getPath() + "." + this.key;
    }

    @Override
    public ConfigRootNode<?> getRoot() {
        return parentNode.getRoot();
    }

    @Override
    public void addChildNode(ConfigNode<?> child) {
        this.children.add(child);
    }

    @Override
    public String getChildPath(String key) {
        return getPath() + "." + key;
    }

}
