package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ClassicConfigNode;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;

import javax.annotation.Nullable;
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
            case "boolean":
                return createNodeWithData(parentNode, key, (object instanceof Boolean) ? object : Boolean.getBoolean((String) object), field);
            case "integer":
                return createNodeWithData(parentNode, key, object instanceof Integer ? object : Integer.valueOf(object.toString()), field);
            case "long":
                return createNodeWithData(parentNode, key, object instanceof Long ? object : Long.valueOf(object.toString()), field);
            case "string":
                return createNodeWithData(parentNode, key, object.toString(), field);
        }

        return createNodeWithData(parentNode, key, object, field);
    }

    public Object createObjectFromNode(ConfigNode<?> node) throws Exception {
        return createObjectFromValue(node.getValue());
    }

    @Override
    public Object createObjectFromValue(Object value) throws Exception {
        return value;
    }

    public static Object createPrimitiveFromObj(Class<?> fieldType, Object object) {
        switch(fieldType.getSimpleName().toLowerCase()) {
            case "short":
                return (object instanceof Short) ? object : Integer.valueOf((int) object).shortValue();
            case "byte":
                return (object instanceof Byte) ? object : Integer.valueOf((int) object).byteValue();
            case "float":
                if (object instanceof Integer)
                    return Double.valueOf((int) object).floatValue();
                else
                    return (object instanceof Float) ? object : Double.valueOf((double) object).floatValue();
            case "boolean":
                return (object instanceof Boolean) ? object : Boolean.getBoolean((String) object);
            case "integer":
                return object instanceof Integer ? object : Integer.valueOf(object.toString());
            case "long":
                return object instanceof Long ? object : Long.valueOf(object.toString());
            case "character":
                return (object instanceof Character) ? object : ((String) object).charAt(0);
            case "string":
                return object.toString();
        }
        return object.toString();
    }

    @Override
    public boolean supports(Class<?> type) {
        return switch (type.getSimpleName().toLowerCase()) {
            case "boolean", "character", "byte", "short", "int", "long", "float", "double","string", "integer" -> true;
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
