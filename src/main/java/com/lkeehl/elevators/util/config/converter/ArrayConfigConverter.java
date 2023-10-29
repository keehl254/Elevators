package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ClassicConfigNode;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;
import org.eclipse.jdt.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception {

        Class<?> singleType = fieldType.getComponentType();
        java.util.List<?> values = new ArrayList<>(object instanceof List ? (List<?>) object : Arrays.asList((Object[]) object));

        ConfigNode<?> myNode = createNodeWithData(parentNode, key, values.toArray(), field);

        ConfigConverter converter = ConfigConverter.getConverter(singleType);
        for(Object obj : values) {
            if(converter != null)
                myNode.getChildren().add(converter.createNodeFromFieldAndObject(parentNode, singleType, obj.toString(), obj, null));
            else
                myNode.getChildren().add(this.createNodeWithData(parentNode,obj.toString(),obj,null));
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
    public boolean supports(Class<?> type) {
        return type.isArray();
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        if(node instanceof ClassicConfigNode<?> classicNode) {
            Class<?> singleType = classicNode.getField().getType().getComponentType();
            return singleType.getSimpleName() +" Array";
        }
        return "Array";
    }

}
