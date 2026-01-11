package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ConfigNode;

public class ComponentConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, FieldData fieldData) throws Exception {

        ILocaleComponent newComponent = MessageHelper.getLocaleComponent(object.toString());
        return createNodeWithData(parentNode, key, newComponent, fieldData.getField());
    }

    @Override
    public Object serializeNodeToObject(ConfigNode<?> node) throws Exception {
        Object value = node.getValue();
        if(value instanceof ILocaleComponent comp)
            return comp.serialize();
        return value;
    }

    @Override
    public Object serializeValueToYamlObject(Object value) throws Exception {
        if(value instanceof ILocaleComponent comp)
            return comp.serialize();
        return "";
    }

    @Override
    public boolean supports(Class<?> type) {
        return ILocaleComponent.class.isAssignableFrom(type);
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        return "TextComponent";
    }

}