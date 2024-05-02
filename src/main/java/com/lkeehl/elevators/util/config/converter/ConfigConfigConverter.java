package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.util.config.BlankConfig;
import com.lkeehl.elevators.util.config.Comments;
import com.lkeehl.elevators.util.config.Config;
import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ConfigConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception {

        if (!(object instanceof Config) || object.getClass().isInterface()) {
            if (fieldType == Config.class) {
                Object rawData = parentNode.getRoot().getObjectAtPath(key, new HashMap<>());
                object = new BlankConfig(rawData);
            } else {
                object = fieldType.getConstructor().newInstance();
            }
        }


        ConfigNode<?> myNode = this.createNodeWithData(parentNode, key, object, field);
        if (myNode.getValue() instanceof Config config)
            config.setKey(key);

        this.constructMapToConfig(parentNode, myNode, object, fieldType);

        if (myNode.getValue() instanceof Config config)
            config.onLoad();

        return myNode;
    }

    public void constructMapToConfig(ConfigNode<?> parentNode, ConfigNode<?> myNode, Object object, Class<?> fieldType) throws Exception {
        List<Field> fields = new ArrayList<>();
        Class<?> current = fieldType;
        while (current != null) {
            fields.addAll(List.of(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        for (Field childField : fields) {
            if (Modifier.isTransient(childField.getModifiers()) || Modifier.isFinal(childField.getModifiers()))
                continue;

            if (!childField.trySetAccessible())
                continue;

            String path = childField.getName();

            Object obj = childField.get(myNode.getValue());
            if (obj == null) {
                try {
                    obj = childField.getType().getConstructor().newInstance();
                } catch (Exception e) {
                    if (Map.class.isAssignableFrom(childField.getType()))
                        obj = new HashMap<>();
                    else if (Set.class.isAssignableFrom(childField.getType()))
                        obj = new HashSet<>();
                    else if (childField.isEnumConstant())
                        obj = childField.getType().getEnumConstants()[0];
                    else
                        continue;
                }
            }
            obj = parentNode.getRoot().getObjectAtPath(myNode.getChildPath(path), obj);

            ConfigConverter converter = ConfigConverter.getConverter(childField.getType());
            ConfigNode<?> childNode;
            if (converter == null)
                childNode = this.createNodeWithData(myNode, path, obj, childField);
            else
                childNode = converter.createNodeFromFieldAndObject(myNode, childField.getType(), path, obj, childField);

            if (childField.isAnnotationPresent(Comments.class)) {
                Comments comments = childField.getAnnotation(Comments.class);
                for (String comment : comments.value())
                    childNode.addComment(comment);
            }

            myNode.getChildren().add(childNode);
        }

    }

    @Override()
    public Object createObjectFromNode(ConfigNode<?> node) throws Exception {

        ((Config) node.getValue()).onSave();

        if (node.getValue() instanceof BlankConfig blankConfig)
            return blankConfig.convertToObject();

        LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();
        for (ConfigNode<?> childNode : node.getChildren()) {
            Object value = childNode.getValue();

            ConfigConverter converter = ConfigConverter.getConverter(value.getClass());
            if (converter != null)
                value = converter.createObjectFromNode(childNode);

            newMap.put(childNode.getKey(), value);
        }

        return newMap;
    }

    @Override
    public Object createObjectFromValue(Object configObj) throws Exception {

        if (!(configObj instanceof Config config))
            return new HashMap<>();

        config.onSave();

        if (configObj instanceof BlankConfig blankConfig)
            return blankConfig.convertToObject();

        LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();

        List<Field> fields = new ArrayList<>();
        Class<?> current = configObj.getClass();
        while (current != null) {
            fields.addAll(List.of(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        for (Field childField : fields) {
            if (Modifier.isTransient(childField.getModifiers()) || Modifier.isFinal(childField.getModifiers()))
                continue;

            if (!childField.trySetAccessible())
                continue;

            String path = childField.getName();

            Object obj = childField.get(configObj);
            if (obj == null) {
                try {
                    obj = childField.getType().getConstructor().newInstance();
                } catch (Exception e) {
                    if (Map.class.isAssignableFrom(childField.getType()))
                        obj = new HashMap<>();
                    else if (Set.class.isAssignableFrom(childField.getType()))
                        obj = new HashSet<>();
                    else if (childField.isEnumConstant())
                        obj = childField.getType().getEnumConstants()[0];
                    else
                        continue;
                }
            }
            if (obj == null)
                continue;

            ConfigConverter converter = ConfigConverter.getConverter(obj.getClass());
            if (converter != null)
                obj = converter.createObjectFromValue(obj);

            newMap.put(path, obj);

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
