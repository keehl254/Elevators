package com.lkeehl.elevators.util.config.nodes;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ResourceHelper;
import com.lkeehl.elevators.util.config.ConfigConverter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;

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
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed to load config node data. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
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
