package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.api.util.config.RecipeRow;
import me.keehl.elevators.api.util.config.converter.IFieldData;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ClassicConfigNode;
import me.keehl.elevators.api.util.config.nodes.ConfigNode;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, IFieldData fieldData) throws Exception {

        ConfigConverter converter = null;
        IFieldData childFieldData = fieldData.getGenericData()[0];
        if(childFieldData != null)
            converter = getConverter(childFieldData.getFieldClass());

        List<Object> values = new ArrayList<>();
        ConfigNode<?> myNode = createNodeWithData(parentNode, key, values, fieldData.getField());

        for (Object obj : (Collection<?>) object) {
            if (converter != null) {
                ConfigNode<?> childNode = converter.deserializeNodeWithFieldAndObject(parentNode, obj.toString(), obj, childFieldData);
                values.add(childNode.getValue());
                myNode.getChildren().add(childNode);
            } else
                myNode.getChildren().add(ConfigConverter.createNodeWithData(parentNode, obj.toString(), obj, null));
        }

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
    public Object serializeValueToYamlObject(Object listObj) throws Exception {

        if(!(listObj instanceof List<?> list))
            return new ArrayList<>();

        List<Object> values = new ArrayList<>();
        for(Object item : list) {
            ConfigConverter converter = ConfigConverter.getConverter(item.getClass());
            if (converter != null)
                item = converter.serializeValueToYamlObject(item);
            values.add(item);
        }
        return values;
    }

    @Override
    public boolean supports(Class<?> type) {
        return List.class.isAssignableFrom(type) && !RecipeRow.class.isAssignableFrom(type);
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
