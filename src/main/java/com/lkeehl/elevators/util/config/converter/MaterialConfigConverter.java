package com.lkeehl.elevators.util.config.converter;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.util.config.ConfigConverter;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class MaterialConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) {
        return createNodeWithData(parentNode, key, Material.matchMaterial(object.toString()), field);
    }

    public Object createObjectFromNode(ConfigNode<?> node) throws Exception {
        return createObjectFromValue(node.getValue());
    }

    @Override
    public Object createObjectFromValue(Object value) throws Exception {
        Material materialValue = (Material) value;
        return materialValue != null ? materialValue.getKey().asString() : null;
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.isAssignableFrom(Material.class);
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        return "Material";
    }

}
