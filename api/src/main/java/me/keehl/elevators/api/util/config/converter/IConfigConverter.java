package me.keehl.elevators.api.util.config.converter;

import me.keehl.elevators.api.util.config.nodes.ConfigNode;

public interface IConfigConverter {

    ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, IFieldData fieldData) throws Exception;

    Object serializeNodeToObject(ConfigNode<?> node) throws Exception;

    Object serializeValueToYamlObject(Object value) throws Exception;

    boolean supports(Class<?> type);

    String getFieldDisplay(ConfigNode<?> node);

}
