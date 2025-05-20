package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ClassicConfigNode;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception {

        ConfigConverter converter = null;
        Class<?> genericClazz = null;
        if (field != null) {
            ParameterizedType genericType = (field.getGenericType() instanceof ParameterizedType) ? (ParameterizedType) field.getGenericType() : null;
            if (genericType != null) {
                genericClazz = Elevators.getInstance().getClass().getClassLoader().loadClass(genericType.getActualTypeArguments()[0].getTypeName());
                converter = ConfigConverter.getConverter(genericClazz);
            }
        }

        List<Object> values = new ArrayList<>((Collection<?>) object);
        ConfigNode<?> myNode = createNodeWithData(parentNode, key, values, field);

        for (Object obj : values) {
            if (converter != null)
                myNode.getChildren().add(converter.createNodeFromFieldAndObject(parentNode, genericClazz, obj.toString(), obj, null));
            else
                myNode.getChildren().add(this.createNodeWithData(parentNode, obj.toString(), obj, null));
        }

        return myNode;
    }

    public Object createObjectFromNode(ConfigNode<?> node) throws Exception {

        List<Object> values = new ArrayList<>();
        for(ConfigNode<?> childNode : node.getChildren()) {
            Object value = childNode.getValue();

            ConfigConverter converter = ConfigConverter.getConverter(value.getClass());
            if(converter != null)
                value = converter.createObjectFromNode(childNode);

            values.add(value);
        }

        return values;
    }

    @Override
    public Object createObjectFromValue(Object listObj) throws Exception {

        if(!(listObj instanceof List<?> list))
            return new ArrayList<>();

        List<Object> values = new ArrayList<>();
        for(Object item : list) {
            ConfigConverter converter = ConfigConverter.getConverter(item.getClass());
            if (converter != null)
                item = converter.createObjectFromValue(item);
            values.add(item);
        }
        return values;
    }

    @Override
    public boolean supports(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        if(node instanceof ClassicConfigNode<?> classicNode) {
            ParameterizedType genericType = (classicNode.getField().getGenericType() instanceof ParameterizedType) ? (ParameterizedType) classicNode.getField().getGenericType() : null;
            if (genericType != null)
                return genericType.getClass().getSimpleName() + " Array";
        }
        return "Array";
    }

}
