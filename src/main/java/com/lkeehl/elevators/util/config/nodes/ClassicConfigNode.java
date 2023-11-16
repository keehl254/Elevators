package com.lkeehl.elevators.util.config.nodes;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.util.config.ConfigConverter;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ClassicConfigNode<T> implements ConfigNode<T> {

    private final ConfigNode<?> parentNode;

    private final Field field;

    private final ArrayList<ConfigNode<?>> children = new ArrayList<>();

    public ClassicConfigNode(ConfigNode<?> parentNode, Field field, T value) {
        this.parentNode = parentNode;
        this.field = field;

        if(value == null)
            return;

        this.field.setAccessible(true);
        try {
            this.field.set(parentNode.getValue(), value);
        } catch (Exception e) {
            Elevators.getElevatorsLogger().warning("Config input at path '" + this.getPath() +"' must be of type '" + this.getFieldDisplay()+"'. Default value has been substituted.");
        }
    }

    @Override
    public String getKey() {
        return field.getName().replace("_",".");
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getValue() {
        try {
            this.field.setAccessible(true);
            return (T) field.get(parentNode.getValue());
        } catch (IllegalAccessException e) {
            Elevators.getElevatorsLogger().warning("There was a problem pulling config data during loading or saving. This is not an issue with your config as much as it is a bug. Please report the follow:");
            e.printStackTrace();
            return null;
        }
    }

    public Field getField() {
        return this.field;
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
        return ConfigConverter.getConverter(this.field.getType());
    }

    public String getFieldDisplay() {
        return this.getConfigConverter().getFieldDisplay(this);
    }

    @Override
    public String getPath() {
        if(parentNode instanceof ConfigRootNode)
            return this.field.getName();
        return parentNode.getPath() + "." + this.field.getName();
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
