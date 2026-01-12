package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.api.util.config.converter.IFieldData;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.api.util.config.nodes.ConfigNode;
import org.bukkit.Material;

public class MaterialConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, IFieldData fieldData) {
        return ConfigConverter.createNodeWithData(parentNode, key, Material.matchMaterial(object.toString()), fieldData.getField());
    }

    public Object serializeNodeToObject(ConfigNode<?> node) throws Exception {
        return serializeValueToYamlObject(node.getValue());
    }

    @Override
    public Object serializeValueToYamlObject(Object value) throws Exception {
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
