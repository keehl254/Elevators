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

        for (Field childField : fieldType.getDeclaredFields()) {
            if (Modifier.isTransient(childField.getModifiers()) || Modifier.isFinal(childField.getModifiers()))
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
                    else if(childField.isEnumConstant())
                        obj = childField.getType().getEnumConstants()[0];
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

            myNode.getChildren().add(childNode);
        }
    }

    public Object createObjectFromNode(ConfigNode<?> node) throws Exception {

        LinkedHashMap<String,Object> newMap = new LinkedHashMap<>();
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

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        return "Config";
    }

}
