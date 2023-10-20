package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.util.config.Comments;
import com.lkeehl.elevators.util.config.Config;
import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;
import org.eclipse.jdt.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ConfigConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception {

        object = fieldType.getConstructor().newInstance();

        ConfigNode<?> myNode = this.createNodeWithData(parentNode, key, object,field);

        this.constructMapToConfig(parentNode, myNode, object, fieldType);

        return myNode;
    }

    public void constructMapToConfig(ConfigNode<?> parentNode, ConfigNode<?> myNode, Object object, Class<?> fieldType) throws Exception {
        List<ConfigNode<?>> children = new ArrayList<>();

        for (Field childField : fieldType.getDeclaredFields()) {
            if (doSkip(childField)) //TODO: Fix up
                continue;

            if (Modifier.isPrivate(childField.getModifiers()))
                childField.setAccessible(true);

            String path = childField.getName();

            Object obj = childField.get(myNode.getValue());
            if(obj == null) {
                try {
                    obj = childField.getType().getConstructor().newInstance();
                }catch (Exception e) {
                    if(Map.class.isAssignableFrom(childField.getType()))
                        obj = new HashMap<>();
                    else if(Set.class.isAssignableFrom(childField.getType()))
                        obj = new HashSet<>();
                    else
                        continue;
                }
            }
            obj = parentNode.getRoot().getObjectAtPath(myNode.getChildPath(path), obj);

            ConfigConverter converter = ConfigConverter.getConverter(childField.getType());
            ConfigNode<?> childNode;
            if(converter == null)
                childNode = this.createNodeWithData(myNode, path, obj, childField);
            else
                childNode = converter.createNodeFromFieldAndObject(myNode, childField.getType(), path, obj, childField);

            if (childField.isAnnotationPresent(Comments.class)) {
                Comments comments = childField.getAnnotation(Comments.class);
                for(String comment : comments.value())
                    childNode.addComment(comment);
            }

            children.add(childNode);

        }
        myNode.getChildren().addAll(children);
    }

    public Object createObjectFromNode(ConfigNode<?> node) throws Exception {

        Map<String,Object> newMap = new HashMap<>();
        for(ConfigNode<?> childNode : node.getChildren()) {
            Object value = childNode.getValue();

            ConfigConverter converter = ConfigConverter.getConverter(value.getClass());
            if(converter != null)
                value = converter.createObjectFromNode(childNode);

            newMap.put(childNode.getKey(), value);
        }

        return newMap;
    }

    @Override
    public boolean supports(Class<?> type) {
        return Config.class.isAssignableFrom(type);
    }

    protected boolean doSkip(Field field) {
        return Modifier.isTransient(field.getModifiers()) || Modifier.isFinal(field.getModifiers());
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        return "Config";
    }

}
