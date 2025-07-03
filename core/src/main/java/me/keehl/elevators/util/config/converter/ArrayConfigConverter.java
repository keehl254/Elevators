package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ClassicConfigNode;
import me.keehl.elevators.util.config.nodes.ConfigNode;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArrayConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, FieldData fieldData) throws Exception {

        FieldData childFieldData = fieldData.getGenericData()[0];
        if(childFieldData == null)
            return null;

        ConfigConverter converter = ConfigConverter.getConverter(childFieldData.getFieldClass());
        List<?> currentValues = new ArrayList<>(object instanceof List ? (List<?>) object : Arrays.asList((Object[]) object));
        List<Object> values = new ArrayList<>();

        List<ConfigNode<?>> childrenNodes = new ArrayList<>();

        for (Object obj : currentValues) {
            if (converter != null) {
                ConfigNode<?> childNode = converter.deserializeNodeWithFieldAndObject(parentNode, obj.toString(), obj, childFieldData);
                values.add(childNode.getValue());
                childrenNodes.add(childNode);
            } else
                childrenNodes.add(this.createNodeWithData(parentNode, obj.toString(), obj, null));
        }

        ConfigNode<?> myNode = createNodeWithData(parentNode, key, values.toArray(), fieldData.getField());
        myNode.getChildren().addAll(childrenNodes);

        return myNode;
    }

    @Override()
    public Object serializeNodeToObject(ConfigNode<?> node) throws Exception {

        List<Object> values = new ArrayList<>();
        for (ConfigNode<?> childNode : node.getChildren()) {
            Object value = childNode.getValue();

            ConfigConverter converter = ConfigConverter.getConverter(value.getClass());
            if (converter != null)
                value = converter.serializeNodeToObject(childNode);

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
    public Object serializeValueToObject(Object arrayObj) throws Exception {
        Object[] array = this.convertToObjectArray(arrayObj);

        List<Object> values = new ArrayList<>();
        for (Object item : array) {
            ConfigConverter converter = ConfigConverter.getConverter(item.getClass());
            if (converter != null)
                item = converter.serializeValueToObject(item);
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
        if (node instanceof ClassicConfigNode<?>) {
            ClassicConfigNode<?> classicNode = (ClassicConfigNode<?>) node;
            Class<?> singleType = classicNode.getField().getType().getComponentType();
            return singleType.getSimpleName() + " Array";
        }
        return "Array";
    }

}
