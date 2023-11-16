package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ClassicConfigNode;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;
import org.eclipse.jdt.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class EnumConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception {

        if(object instanceof String strValue) {
            Optional<?> objectOpt = Arrays.stream(fieldType.getEnumConstants()).filter(i -> i.toString().equalsIgnoreCase(strValue)).findFirst();
            if(objectOpt.isPresent())
                object = objectOpt.get();
            else {
                Elevators.getElevatorsLogger().warning("Value at path \"" + parentNode.getPath() + "\" must be a \"" + fieldType.getSimpleName()+"\" enum value! Using default: \"" + object.toString() + "\".");
                object = fieldType.getEnumConstants()[0];
            }
        }else if(!object.getClass().isEnum()) {
            object = fieldType.getEnumConstants()[0];
            Elevators.getElevatorsLogger().warning("Value at path \"" + parentNode.getPath() + "\" must be a \"" + fieldType.getSimpleName()+"\" enum value! Using default: \"" + object.toString() + "\".");
        }

        return createNodeWithData(parentNode, key, object, field);
    }

    public Object createObjectFromNode(ConfigNode<?> node) {
        return node.getValue().toString();
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.isEnum();
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        if(node instanceof ClassicConfigNode<?> classicNode)
            return classicNode.getField().getClass().getSimpleName();
        return "Enum";
    }

}
