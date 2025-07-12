package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.util.config.*;
import me.keehl.elevators.util.config.nodes.ConfigNode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ConfigConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, FieldData fieldData) throws Exception {

        Object rawData = parentNode.getRoot().getObjectAtPath(key, new HashMap<>());
        if (!(object instanceof Config) || object.getClass().isInterface()) {
            if (fieldData.getFieldClass() == Config.class) {
                object = new BlankConfig(rawData);
            } else {
                object = fieldData.getFieldClass().getConstructor().newInstance();
            }
        }

        ConfigNode<?> myNode = ConfigConverter.createNodeWithData(parentNode, key, object, fieldData.getField());
        if (myNode.getValue() instanceof Config)
            ((Config) myNode.getValue()).setKey(key);

        this.constructMapToConfig(parentNode, myNode, rawData, fieldData);

        if (myNode.getValue() instanceof Config)
            ((Config) myNode.getValue()).onLoad();

        return myNode;
    }

    public void constructMapToConfig(ConfigNode<?> parentNode, ConfigNode<?> myNode, Object rawData, FieldData fieldData) throws Exception {
        Config configObj = (Config) myNode.getValue();

        List<Field> fields = new ArrayList<>();
        Class<?> current = fieldData.getFieldClass();
        while (current != null) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        for (Field childField : fields) {
            if (Modifier.isTransient(childField.getModifiers()) || Modifier.isFinal(childField.getModifiers()))
                continue;

            childField.setAccessible(true);
            FieldData childFieldData = new FieldData(childField);

            ConfigFieldName fieldName = childField.getAnnotation(ConfigFieldName.class);
            String path = fieldName != null ? fieldName.value() : childField.getName();

            Object obj = childField.get(configObj);
            if (obj == null) {
                try {
                    obj = childField.getType().getConstructor().newInstance();
                } catch (Exception e) {
                    if (Map.class.isAssignableFrom(childField.getType()))
                        obj = new HashMap<>();
                    else if (Set.class.isAssignableFrom(childField.getType()))
                        obj = new HashSet<>();
                    else if (List.class.isAssignableFrom(childField.getType()))
                        obj = new ArrayList<>();
                    else if (childField.isEnumConstant())
                        obj = childField.getType().getEnumConstants()[0];
                    else {
                        continue;
                    }
                }
            }
            obj = parentNode.getRoot().getObjectAtPath(myNode.getChildPath(path), obj);

            ConfigConverter converter = ConfigConverter.getConverter(childField.getType());
            ConfigNode<?> childNode;
            if (converter != null)
                childNode = converter.deserializeNodeWithFieldAndObject(myNode, path, obj, childFieldData);
            else
                childNode = ConfigConverter.createNodeWithData(myNode, path, obj, childField);

            if (childField.isAnnotationPresent(Comments.class)) {
                Comments comments = childField.getAnnotation(Comments.class);
                for (String comment : comments.value())
                    childNode.addComment(comment);
            }

            myNode.getChildren().add(childNode);

            if (configObj instanceof ExpandableConfig)
                ((ExpandableConfig) configObj).data.put(path, childNode);
        }

        if (!(configObj instanceof ExpandableConfig))
            return;

        ExpandableConfig expandableConfig = (ExpandableConfig) configObj;
        expandableConfig.parentNode = myNode;

        if (!(rawData instanceof Map<?, ?>))
            return;
        Map<?, ?> mapData = (Map<?, ?>) rawData;

        for (Object objKey : mapData.keySet()) {
            String key = objKey.toString();
            Object obj = mapData.get(key);

            FieldData childFieldData = new FieldData(null, obj.getClass(), obj.getClass());

            ConfigConverter converter = ConfigConverter.getConverter(obj.getClass());
            ConfigNode<?> childNode;
            if (converter != null)
                childNode = converter.deserializeNodeWithFieldAndObject(myNode, key, obj, childFieldData);
            else
                childNode = ConfigConverter.createNodeWithData(myNode, key, obj, null);

            myNode.getChildren().add(childNode);
            expandableConfig.setData(key, childNode.getValue());
        }
    }

    @Override()
    public Object serializeNodeToObject(ConfigNode<?> node) throws Exception {

        ((Config) node.getValue()).onSave();

        if (node.getValue() instanceof BlankConfig)
            return ((BlankConfig) node.getValue()).convertToObject();

        LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();
        for (ConfigNode<?> childNode : node.getChildren()) {
            Object value = childNode.getValue();

            ConfigConverter converter = ConfigConverter.getConverter(value.getClass());
            if (converter != null)
                value = converter.serializeNodeToObject(childNode);

            newMap.put(childNode.getKey(), value);
        }

        return newMap;
    }

    @Override
    public Object serializeValueToObject(Object configObj) throws Exception {

        if (!(configObj instanceof Config))
            return new HashMap<>();

        Config config = (Config) configObj;

        config.onSave();

        if (configObj instanceof BlankConfig)
            return ((BlankConfig) configObj).convertToObject();

        LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();

        List<Field> fields = new ArrayList<>();
        Class<?> current = configObj.getClass();
        while (current != null) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        for (Field childField : fields) {
            if (Modifier.isTransient(childField.getModifiers()) || Modifier.isFinal(childField.getModifiers()))
                continue;

            childField.setAccessible(true);

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
                obj = converter.serializeValueToObject(obj);

            newMap.put(path, obj);

        }

        if (!(configObj instanceof ExpandableConfig))
            return newMap;

        ExpandableConfig expandableConfig = (ExpandableConfig) configObj;

        for (String key : expandableConfig.data.keySet()) {
            ConfigNode<?> childNode = expandableConfig.data.get(key);
            Object obj = childNode.getValue();

            ConfigConverter converter = ConfigConverter.getConverter(obj.getClass());
            if (converter != null)
                obj = converter.serializeValueToObject(obj);

            newMap.put(key, obj);
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
