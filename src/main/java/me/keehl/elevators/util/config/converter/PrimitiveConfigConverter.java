package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.api.util.config.converter.IFieldData;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ClassicConfigNode;
import me.keehl.elevators.api.util.config.nodes.ConfigNode;

public class PrimitiveConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, IFieldData fieldData) {
        switch(fieldData.getFieldClass().getSimpleName().toLowerCase()) {
            case "short":
                return ConfigConverter.createNodeWithData(parentNode, key, (object instanceof Short) ? object : Integer.valueOf((int) object).shortValue(), fieldData.getField());
            case "byte":
                return ConfigConverter.createNodeWithData(parentNode, key, (object instanceof Byte) ? object : Integer.valueOf((int) object).byteValue(), fieldData.getField());
            case "float":
                if (object instanceof Integer)
                    return ConfigConverter.createNodeWithData(parentNode, key, Double.valueOf((int) object).floatValue(), fieldData.getField());
                else
                    return ConfigConverter.createNodeWithData(parentNode, key, (object instanceof Float) ? object : Double.valueOf((double) object).floatValue(), fieldData.getField());
            case "char":
                return ConfigConverter.createNodeWithData(parentNode, key, (object instanceof Character) ? object : ((String) object).charAt(0), fieldData.getField());
            case "boolean":
                return ConfigConverter.createNodeWithData(parentNode, key, (object instanceof Boolean) ? object : Boolean.getBoolean((String) object), fieldData.getField());
            case "integer":
                return ConfigConverter.createNodeWithData(parentNode, key, object instanceof Integer ? object : Integer.valueOf(object.toString()), fieldData.getField());
            case "long":
                return ConfigConverter.createNodeWithData(parentNode, key, object instanceof Long ? object : Long.valueOf(object.toString()), fieldData.getField());
            case "string":
                return ConfigConverter.createNodeWithData(parentNode, key, object.toString(), fieldData.getField());
        }

        return ConfigConverter.createNodeWithData(parentNode, key, object, fieldData.getField());
    }

    public Object serializeNodeToObject(ConfigNode<?> node) throws Exception {
        return serializeValueToYamlObject(node.getValue());
    }

    @Override
    public Object serializeValueToYamlObject(Object value) throws Exception {
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
            case "boolean", "character", "byte", "short", "int", "long", "float", "double", "string", "integer" -> true;
            default -> false;
        };
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        if(node instanceof ClassicConfigNode<?>)
            return ((ClassicConfigNode<?>)node).getField().getClass().getSimpleName();
        return "Primitive";
    }

}
