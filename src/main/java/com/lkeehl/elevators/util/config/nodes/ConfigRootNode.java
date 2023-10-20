package com.lkeehl.elevators.util.config.nodes;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.util.config.Config;
import com.lkeehl.elevators.util.config.ConfigConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigRootNode<T extends Config> implements ConfigNode<T> {

    private final Map<String, List<String>> comments = new HashMap<>();

    private final Map<?,?> data;

    private final T config;

    private final List<ConfigNode<?>> children = new ArrayList<>();

    public ConfigRootNode(Map<?,?> value, T config) {
        this.data = value;
        this.config = config;
    }

    private Object traverseData(Map<?, ?> currentMap, String[] keys, int index) {
        if (index == keys.length)
            return currentMap;
        if(currentMap == null)
            return null;

        String currentKey = keys[index];

        for(Object key : currentMap.keySet()) {
            if(!key.toString().equalsIgnoreCase(currentKey))
                continue;
            Object value = currentMap.get(key);
            if (value instanceof Map)
                return traverseData((Map<?, ?>) value, keys, index + 1);
            else
                return value;
        }

        return null;
    }

    public Object getObjectAtPath(String path, Object defaultValue) {
        String[] keyPath = path.split("\\.");

        Object value = traverseData(this.data, keyPath, 0);
        return value == null ? defaultValue : value;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public T getValue() {
        return this.config;
    }

    public T getConfig() {
        return this.config;
    }

    public Object serializeToObject() {
        Map<Object, Object> map = new HashMap<>();
        for(ConfigNode<?> childNode : this.getChildren()) {
            try {
                map.put(childNode.getKey(), childNode.getConfigConverter().createObjectFromNode(childNode));
            } catch (Exception e) {
                Elevators.getElevatorsLogger().warning("Failed to save config path '" + childNode.getPath()+"'!");
                e.printStackTrace();
            }
        }

        return map;
    }

    @Override
    public List<ConfigNode<?>> getChildren() {
        return this.children;
    }

    @Override()
    public void addComment(String comment) {
        this.addComment("",comment);
    }

    public void addComment(String path, String comment) {
        if(!this.comments.containsKey(path))
            this.comments.put(path, new ArrayList<>());

        this.comments.get(path).add(comment);
    }

    public List<String> getCommentsAtPath(String path) {
        if(this.comments.containsKey(path))
            return this.comments.get(path);
        return new ArrayList<>();
    }

    @Override
    public ConfigConverter getConfigConverter() {
        return null;
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public ConfigRootNode<T> getRoot() {
        return this;
    }

    @Override
    public void addChildNode(ConfigNode<?> child) {
        this.children.add(child);
    }

    @Override
    public String getChildPath(String key) {
        return key;
    }

}
