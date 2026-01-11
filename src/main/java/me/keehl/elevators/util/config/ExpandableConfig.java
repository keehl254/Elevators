package me.keehl.elevators.util.config;

import me.keehl.elevators.api.util.config.Config;
import me.keehl.elevators.util.config.nodes.ClassicConfigNode;
import me.keehl.elevators.util.config.nodes.ConfigNode;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableConfig implements Config {

    public transient ConfigNode<?> parentNode;
    public transient Map<String, ConfigNode<?>> data = new HashMap<>();

    public <T> void setData(String key, T value, List<String> comments) {
        Field field = null;
        if(this.data.containsKey(key)) {
            ConfigNode<?> currentNode = this.data.get(key);
            if(comments == null)
                comments = currentNode.getComments();
            currentNode.clearComments();

            if(currentNode instanceof ClassicConfigNode)
                field = ((ClassicConfigNode<?>) currentNode).getField();
            this.parentNode.getChildren().remove(currentNode);
        }
        ConfigNode<?> newNode = ConfigConverter.createNodeWithData(this.parentNode, key, value, field);
        if(comments != null) {
            for (String comment : comments)
                newNode.addComment(comment);
        }
        this.data.put(key, newNode);
    }

    public <T> void setData(String key, T value) {
        this.setData(key, value, null);
    }

    public <T> T getData(String key) {
        if(!this.data.containsKey(key))
            return null;

        ConfigNode<?> currentNode = this.data.get(key);
        return (T) currentNode.getValue();
    }

}
