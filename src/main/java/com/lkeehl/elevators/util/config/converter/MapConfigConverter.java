package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class MapConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception {
        Map<Object, Object> mapObj = new LinkedHashMap<>();

        ConfigNode<?> myNode = createNodeWithData(parentNode, key, mapObj, field);

        ConfigConverter valueConverter = null;
        Class<?> valueClazz = null;
        Class<?> keyClazz = null;
        if (field != null) {
            keyClazz = Elevators.getInstance().getClass().getClassLoader().loadClass(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
            valueClazz = Elevators.getInstance().getClass().getClassLoader().loadClass(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1].getTypeName());
            Type valueType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
            Class<?> containerParam;
            if (valueType instanceof ParameterizedType parameterizedType) {
                containerParam = (Class<?>) parameterizedType.getRawType();
                if (containerParam.isAssignableFrom(List.class) && containerParam.isInterface()) {
                    containerParam = ArrayList.class;
                }
            } else
                containerParam = (Class<?>) valueType;

            valueConverter = ConfigConverter.getConverter(containerParam);
            valueClazz = containerParam;
        }

        if (object instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
                if (entry.getValue() == null)
                    continue;

                ConfigNode<?> childNode;

                if (valueConverter != null)
                    childNode = valueConverter.createNodeFromFieldAndObject(myNode, valueClazz, entry.getKey().toString(), entry.getValue(), null);
                else
                    childNode = this.createNodeWithData(myNode, entry.getKey().toString(), entry.getValue(), null);

                Object keyObj = null;
                if (keyClazz.isEnum()) {
                    Optional<?> objectOpt = Arrays.stream(keyClazz.getEnumConstants()).filter(i -> i.toString().equalsIgnoreCase(entry.getKey().toString())).findFirst();
                    if (objectOpt.isPresent())
                        keyObj = objectOpt.get();
                }
                if (keyObj == null)
                    keyObj = PrimitiveConfigConverter.createPrimitiveFromObj(keyClazz, entry.getKey());

                myNode.getChildren().add(childNode);
                mapObj.put(keyObj, childNode.getValue());
            }
        } else if (!(object instanceof ArrayList<?>)) {
            Elevators.getElevatorsLogger().warning("An invalid value was entered for key: " + key + ". Expected Map or Empty Array.");
        }

        return myNode;
    }

    public Object createObjectFromNode(ConfigNode<?> node) throws Exception {

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
    public Object createObjectFromValue(Object mapObj) throws Exception {

        if (!(mapObj instanceof Map<?, ?> map))
            return new HashMap<>();

        LinkedHashMap<Object, Object> newMap = new LinkedHashMap<>();

        for (Object key : map.keySet()) {
            Object value = map.get(key);

            ConfigConverter converter = ConfigConverter.getConverter(key.getClass());
            if (converter != null)
                key = converter.createObjectFromValue(key);

            converter = ConfigConverter.getConverter(value.getClass());
            if (converter != null)
                value = converter.createObjectFromValue(value);

            newMap.put(key, value);
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
