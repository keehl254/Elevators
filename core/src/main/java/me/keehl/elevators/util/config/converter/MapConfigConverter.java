package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ConfigNode;

import java.util.*;

public class MapConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, FieldData fieldData) throws Exception {
        Map<Object, Object> mapObj = new LinkedHashMap<>();

        ConfigNode<?> myNode = createNodeWithData(parentNode, key, mapObj, fieldData.getField());

        FieldData[] fieldDataList = fieldData.getGenericData();
        FieldData keyFieldData = fieldDataList[0];
        FieldData valueFieldData = fieldDataList[1];


        ConfigConverter valueConverter = valueFieldData.getFieldClass() != null ? getConverter(valueFieldData.getFieldClass()) : null;

        if (object instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
                if (entry.getValue() == null)
                    continue;

                ConfigNode<?> childNode;

                if (valueConverter != null)
                    childNode = valueConverter.deserializeNodeWithFieldAndObject(myNode, entry.getKey().toString(), entry.getValue(), valueFieldData);
                else
                    childNode = ConfigConverter.createNodeWithData(myNode, entry.getKey().toString(), entry.getValue(), null);

                Object keyObj = null;
                if (keyFieldData.getFieldClass().isEnum()) {
                    Optional<?> objectOpt = Arrays.stream(keyFieldData.getFieldClass().getEnumConstants()).filter(i -> i.toString().equalsIgnoreCase(entry.getKey().toString())).findFirst();
                    if (objectOpt.isPresent())
                        keyObj = objectOpt.get();
                }
                if (keyObj == null)
                    keyObj = PrimitiveConfigConverter.createPrimitiveFromObj(keyFieldData.getFieldClass(), entry.getKey());

                myNode.getChildren().add(childNode);
                mapObj.put(keyObj, childNode.getValue());
            }
        } else if (!(object instanceof ArrayList<?>)) {
            Elevators.getElevatorsLogger().warning("An invalid value was entered for key: " + key + ". Expected Map or Empty Array.");
        }

        return myNode;
    }

    public Object serializeNodeToObject(ConfigNode<?> node) throws Exception {

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
    public Object serializeValueToObject(Object mapObj) throws Exception {

        if (!(mapObj instanceof Map<?, ?>))
            return new HashMap<>();

        Map<?,?> map = (Map<?, ?>) mapObj;

        LinkedHashMap<Object, Object> newMap = new LinkedHashMap<>();

        for (Object key : map.keySet()) {
            Object value = map.get(key);

            ConfigConverter converter = ConfigConverter.getConverter(key.getClass());
            if (converter != null)
                key = converter.serializeValueToObject(key);

            converter = ConfigConverter.getConverter(value.getClass());
            if (converter != null)
                value = converter.serializeValueToObject(value);

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
