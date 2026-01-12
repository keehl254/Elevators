package me.keehl.elevators.util.config.converter;

import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.util.config.converter.IFieldData;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.api.util.config.nodes.ConfigNode;
import org.bukkit.NamespacedKey;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamespacedKeyConfigConverter extends ConfigConverter {

    private static final Pattern VALID = Pattern.compile("([a-z0-9._-]+):([a-z0-9._-]+)");


    @Override
    public ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, IFieldData fieldData) throws Exception {

        if(!(object instanceof String)) {
            ElevatorsAPI.log(Level.WARNING, "Value at path \"" + parentNode.getPath() + "\" must be a string value!");
            return ConfigConverter.createNodeWithData(parentNode, key, null, fieldData.getField());
        }

        String strValue = object.toString();
        Matcher matcher = VALID.matcher(strValue);

        if(!matcher.matches()) {
            ElevatorsAPI.log(Level.WARNING, "Value at path \"" + parentNode.getPath() + "\" must be in a valid NamespacedKey format! Example: minecraft:white_wool");
            return ConfigConverter.createNodeWithData(parentNode, key, null, fieldData.getField());
        }

        // I apologize to papermc and spigot for using a deprecated method lol
        //noinspection deprecation
        return ConfigConverter.createNodeWithData(parentNode, key, new NamespacedKey(matcher.group(1), matcher.group(2)), fieldData.getField());
    }

    @Override
    public Object serializeNodeToObject(ConfigNode<?> node) throws Exception {
        return serializeValueToYamlObject(node.getValue());
    }

    @Override
    public Object serializeValueToYamlObject(Object value) throws Exception {
        return value != null ? value.toString() : null;
    }

    @Override
    public boolean supports(Class<?> type) {
        return NamespacedKey.class.isAssignableFrom(type);
    }

    @Override
    public String getFieldDisplay(ConfigNode<?> node) {
        return "Namespaced Key";
    }

}
