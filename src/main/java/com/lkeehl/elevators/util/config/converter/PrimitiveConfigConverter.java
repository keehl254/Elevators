package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ClassicConfigNode;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;
import org.eclipse.jdt.annotation.Nullable;

import java.lang.reflect.Field;

public class PrimitiveConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception {
        switch(fieldType.getSimpleName().toLowerCase()) {
            case "short":
                return createNodeWithData(parentNode, key, (object instanceof Short) ? object : Integer.valueOf((int) object).shortValue(), field);
            case "byte":
                return createNodeWithData(parentNode, key, (object instanceof Byte) ? object : Integer.valueOf((int) object).byteValue(), field);
            case "float":
                if (object instanceof Integer)
                    return createNodeWithData(parentNode, key, Double.valueOf((int) object).floatValue(), field);
                else
                    return createNodeWithData(parentNode, key, (object instanceof Float) ? object : Double.valueOf((double) object).floatValue(), field);
            case "char":
                return createNodeWithData(parentNode, key, (object instanceof Character) ? object : ((String) object).charAt(0), field);
            case "string":
                return createNodeWithData(parentNode, key, object.toString(), field);
        }

        return createNodeWithData(parentNode, key, object, field);
    }

    public Object createObjectFromNode(ConfigNode<?> node) throws Exception {
        return node.getValue();
    }

    @Override
    public boolean supports(Class<?> type) {
        return switch (type.getSimpleName().toLowerCase()) {
            case "boolean", "char", "byte", "short", "int", "long", "float", "double","string" -> true;
            default -> false;
        };
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        if(node instanceof ClassicConfigNode<?> classicNode)
            return classicNode.getField().getClass().getSimpleName();
        return "Primitive";
    }

}
