package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ClassicConfigNode;
import me.keehl.elevators.util.config.nodes.ConfigNode;

import java.lang.reflect.ParameterizedType;
import java.util.*;

public class SetConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, FieldData fieldData) throws Exception {

        FieldData childData = fieldData.getGenericData()[0];
        ConfigConverter converter = childData != null ? getConverter(childData.getFieldClass()) : null;

        List<Object> values = new ArrayList<>();

        List<ConfigNode<?>> childrenNodes = new ArrayList<>();

        for (Object obj : (Collection<?>) object) {
            if (converter != null) {
                ConfigNode<?> childNode = converter.deserializeNodeWithFieldAndObject(parentNode, obj.toString(), obj, childData);
                values.add(childNode.getValue());
                childrenNodes.add(childNode);
            }else
                childrenNodes.add(ConfigConverter.createNodeWithData(parentNode, obj.toString(), obj, null));
        }
        ConfigNode<?> myNode = ConfigConverter.createNodeWithData(parentNode, key, new HashSet<>(values), fieldData.getField());
        myNode.getChildren().addAll(childrenNodes);

        return myNode;
    }

    public Object serializeNodeToObject(ConfigNode<?> node) throws Exception {

        List<Object> values = new ArrayList<>();
        for(ConfigNode<?> childNode : node.getChildren()) {
            Object value = childNode.getValue();

            ConfigConverter converter = ConfigConverter.getConverter(value.getClass());
            if(converter != null)
                value = converter.serializeNodeToObject(childNode);

            values.add(value);
        }

        return values;
    }

    @Override
    public Object serializeValueToYamlObject(Object setObj) throws Exception {

        if(!(setObj instanceof Set<?>))
            return new HashSet<>();

        Set<?> set = (Set<?>) setObj;

        List<Object> values = new ArrayList<>();
        for(Object item : set) {
            ConfigConverter converter = ConfigConverter.getConverter(item.getClass());
            if (converter != null)
                item = converter.serializeValueToYamlObject(item);
            values.add(item);
        }
        return values;
    }

    @Override
    public boolean supports(Class<?> type) {
        return Set.class.isAssignableFrom(type);
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        if(node instanceof ClassicConfigNode<?>) {
            ClassicConfigNode<?> classicNode = (ClassicConfigNode<?>) node;
            ParameterizedType genericType = (classicNode.getField().getGenericType() instanceof ParameterizedType) ? (ParameterizedType) classicNode.getField().getGenericType() : null;
            if (genericType != null)
                return genericType.getClass().getSimpleName() + " Array";
        }
        return "Array";
    }

}
