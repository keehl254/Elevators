package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;
import org.eclipse.jdt.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception {
        Map<Object,Object> mapObj = new HashMap<>();
        List<ConfigNode<?>> children = new ArrayList<>();

        ConfigNode<?> myNode = createNodeWithData(parentNode, key, mapObj, field);

        ConfigConverter converter = null;
        Class<?> valueClazz = null;
        if(field != null) {
            valueClazz = ((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1]);
            converter = ConfigConverter.getConverter(valueClazz);
        }

        for (java.util.Map.Entry<?, ?> entry : ((Map<?,?>) object).entrySet()) {
            if (entry.getValue() == null) continue;
            Class<?> clazz = entry.getValue().getClass();

            ConfigNode<?> childNode;

            if(converter != null)
                childNode = converter.createNodeFromFieldAndObject(myNode, valueClazz == null ? clazz : valueClazz, entry.getKey().toString(), entry.getValue(), null);
            else
                childNode = this.createNodeWithData(myNode,entry.getKey().toString(), entry.getValue(), null);

            children.add(childNode);
            mapObj.put(entry.getKey(), childNode.getValue());
        }
        myNode.getChildren().addAll(children);

        return myNode;
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
        return Map.class.isAssignableFrom(type);
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        return "Map";
    }

}
