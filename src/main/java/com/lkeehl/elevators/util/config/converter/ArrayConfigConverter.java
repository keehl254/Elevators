package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ClassicConfigNode;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception {

        Class<?> singleType = fieldType.getComponentType();
        List<?> values = new ArrayList<>(object instanceof List ? (List<?>) object : Arrays.asList((Object[]) object));

        ConfigNode<?> myNode = createNodeWithData(parentNode, key, values.toArray(), field);

        ConfigConverter converter = ConfigConverter.getConverter(singleType);
        for (Object obj : values) {
            if (converter != null)
                myNode.getChildren().add(converter.createNodeFromFieldAndObject(parentNode, singleType, obj.toString(), obj, null));
            else
                myNode.getChildren().add(this.createNodeWithData(parentNode, obj.toString(), obj, null));
        }

        return myNode;
    }

    @Override()
    public Object createObjectFromNode(ConfigNode<?> node) throws Exception {

        List<Object> values = new ArrayList<>();
        for (ConfigNode<?> childNode : node.getChildren()) {
            Object value = childNode.getValue();

            ConfigConverter converter = ConfigConverter.getConverter(value.getClass());
            if (converter != null)
                value = converter.createObjectFromNode(childNode);

            values.add(value);
        }

        return values;
    }

    private Object[] convertToObjectArray(Object array) {
        Class<?> ofArray = array.getClass().getComponentType();
        if (ofArray.isPrimitive()) {
            List<Object> ar = new ArrayList<>();
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++)
                ar.add(Array.get(array, i));
            return ar.toArray();
        } else {
            return (Object[]) array;
        }
    }

    @Override
    public Object createObjectFromValue(Object arrayObj) throws Exception {
        Object[] array = this.convertToObjectArray(arrayObj);

        List<Object> values = new ArrayList<>();
        for (Object item : array) {
            ConfigConverter converter = ConfigConverter.getConverter(item.getClass());
            if (converter != null)
                item = converter.createObjectFromValue(item);
            values.add(item);
        }
        return values;
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.isArray();
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        if (node instanceof ClassicConfigNode<?> classicNode) {
            Class<?> singleType = classicNode.getField().getType().getComponentType();
            return singleType.getSimpleName() + " Array";
        }
        return "Array";
    }

}
