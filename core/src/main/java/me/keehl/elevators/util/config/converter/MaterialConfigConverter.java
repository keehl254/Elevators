package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ConfigNode;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class MaterialConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, FieldData fieldData) {
        return createNodeWithData(parentNode, key, Material.matchMaterial(object.toString()), fieldData.getField());
    }

    public Object serializeNodeToObject(ConfigNode<?> node) throws Exception {
        return serializeValueToObject(node.getValue());
    }

    @Override
    public Object serializeValueToObject(Object value) throws Exception {
        Material materialValue = (Material) value;
        return materialValue != null ? materialValue.getKey().toString() : null;
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
