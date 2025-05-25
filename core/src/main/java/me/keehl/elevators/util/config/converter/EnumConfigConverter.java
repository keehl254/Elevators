package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ClassicConfigNode;
import me.keehl.elevators.util.config.nodes.ConfigNode;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class EnumConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) {

        if(object instanceof String) {
            String strValue = object.toString();
            Optional<?> objectOpt = Arrays.stream(fieldType.getEnumConstants()).filter(i -> i.toString().equalsIgnoreCase(strValue)).findFirst();
            if(objectOpt.isPresent())
                object = objectOpt.get();
            else {
                Elevators.getElevatorsLogger().warning("Value at path \"" + parentNode.getPath() + "\" must be a \"" + fieldType.getSimpleName()+"\" enum value! Using default: \"" + object + "\".");
                object = fieldType.getEnumConstants()[0];
            }
        }else if(!object.getClass().isEnum()) {
            object = fieldType.getEnumConstants()[0];
            Elevators.getElevatorsLogger().warning("Value at path \"" + parentNode.getPath() + "\" must be a \"" + fieldType.getSimpleName()+"\" enum value! Using default: \"" + object.toString() + "\".");
        }

        return createNodeWithData(parentNode, key, object, field);
    }

    public Object createObjectFromNode(ConfigNode<?> node) throws Exception {
        return createObjectFromValue(node.getValue());
    }

    @Override
    public Object createObjectFromValue(Object value) throws Exception {
        return value.toString();
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.isEnum();
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        if(node instanceof ClassicConfigNode<?>)
            return ((ClassicConfigNode<Object>) node).getField().getClass().getSimpleName();
        return "Enum";
    }

}
