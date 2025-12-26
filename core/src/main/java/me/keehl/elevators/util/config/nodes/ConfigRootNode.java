package me.keehl.elevators.util.config.nodes;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.util.config.Config;
import me.keehl.elevators.util.config.ConfigConverter;

import java.util.*;
import java.util.logging.Level;

public class ConfigRootNode<T extends Config> implements ConfigNode<T> {

    private final Map<String, List<String>> comments = new HashMap<>();

    private final Map<?, ?> data;

    private final T config;

    private final ArrayList<ConfigNode<?>> children = new ArrayList<>();

    public ConfigRootNode(Map<?, ?> value, T config) {
        this.data = value;
        this.config = config;
    }

    private Object traverseData(Map<?, ?> currentMap, String[] keys, int index) {
        if (index == keys.length)
            return currentMap;
        if (currentMap == null)
            return null;

        String currentKey = keys[index];

        for (Object key : currentMap.keySet()) {
            if (!key.toString().equalsIgnoreCase(currentKey))
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

        ConfigConverter converter = ConfigConverter.getConverter(this.config.getClass());
        if (converter != null) {
            try {
                return converter.serializeValueToObject(this.config);
            } catch (Exception ignored) {
            }
        }

        Elevators.log(Level.WARNING, "Failed to convert main root node! Using old data.");

        LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
        for (ConfigNode<?> childNode : this.getChildren()) {
            try {
                map.put(childNode.getKey(), childNode.getConfigConverter().serializeNodeToObject(childNode));
            } catch (Exception e) {
                Elevators.log(Level.SEVERE, "Failed to save config path \"" + childNode.getPath() + ".\". Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
            }
        }

        return map;
    }

    @Override
    public ArrayList<ConfigNode<?>> getChildren() {
        return this.children;
    }

    @Override()
    public void addComment(String comment) {
        this.addComment("", comment);
    }

    @Override
    public List<String> getComments() {
        throw new RuntimeException("GetComments called on RootNode");
    }

    @Override
    public void clearComments() {
        this.comments.clear();
    }

    public void addComment(String path, String comment) {
        if (!this.comments.containsKey(path))
            this.comments.put(path, new ArrayList<>());

        this.comments.get(path).add(comment);
    }

    public List<String> getCommentsAtPath(String path) {
        if (this.comments.containsKey(path))
            return this.comments.get(path);
        return new ArrayList<>();
    }

    public void clearCommentsAtPath(String path) {
        this.comments.remove(path);
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
