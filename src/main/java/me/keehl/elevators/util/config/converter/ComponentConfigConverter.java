package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.util.config.converter.IFieldData;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.AdventureLocaleComponent;
import me.keehl.elevators.models.BungeeLocaleComponent;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.api.util.config.nodes.ConfigNode;

public class ComponentConfigConverter extends ConfigConverter {

    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, IFieldData fieldData) throws Exception {

        ILocaleComponent newComponent;
        if(object instanceof ILocaleComponent) {
            newComponent = (ILocaleComponent) object;
        } else {
            newComponent = MessageHelper.getLocaleComponent(object.toString());
        }
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
        return ILocaleComponent.class.isAssignableFrom(type) || AdventureLocaleComponent.class.isAssignableFrom(type) || BungeeLocaleComponent.class.isAssignableFrom(type);
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        return "TextComponent";
    }

}