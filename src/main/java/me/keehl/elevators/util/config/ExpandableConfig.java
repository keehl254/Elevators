package me.keehl.elevators.util.config;

import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.util.config.Config;
import me.keehl.elevators.api.util.config.converter.IFieldData;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.util.config.nodes.ClassicConfigNode;
import me.keehl.elevators.api.util.config.nodes.ConfigNode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/*
    ExpandableConfigs like this not only set field values to matching yml, but will also set a key value pair inside
    the ExpandableConfig. The setData and getData methods can be used to access this info. This data is then included
    in the created YAML hashmap upon save.

    This is useful for scenarios where the config members are fluid / not known at compile time.
 */
public class ExpandableConfig implements Config {

    public transient ConfigNode<?> parentNode;
    public transient Map<String, ConfigNode<?>> data = new HashMap<>();
    private transient String key;

    private transient ConfigNode<?> syncedNode = null;

    public <T> void setData(String key, T value, List<String> comments) {
        Field field = null;
        if (this.data.containsKey(key)) {
            ConfigNode<?> currentNode = this.data.get(key);
            if (comments == null)
                comments = currentNode.getComments();
            currentNode.clearComments();

            if (currentNode instanceof ClassicConfigNode) {
                field = ((ClassicConfigNode<?>) currentNode).getField();
            }
            this.parentNode.getChildren().remove(currentNode);
        }

        ConfigNode<?> newNode = null;
        Class<?> fieldClazz = field != null ? field.getType() : value.getClass();
        ConfigConverter converter = ConfigConverter.getConverter(fieldClazz);
        if(converter != null) {
            IFieldData fieldData = new ConfigConverter.FieldData(field, fieldClazz, fieldClazz);
            try {
                newNode = converter.deserializeNodeWithFieldAndObject(this.parentNode, key, value, fieldData);
            }catch (Exception e) {
                ElevatorsAPI.log(Level.SEVERE, "", e);
            }
        }
        if(newNode == null) {
            newNode = ConfigConverter.createNodeWithData(this.parentNode, key, value, field);
        }

        if (comments != null) {
            for (String comment : comments) {
                newNode.addComment(comment);
            }
        }
        this.data.put(key, newNode);
    }

    public <T> void setData(String key, T value) {
        this.setData(key, value, null);
    }

    public void clearData() {
        List<String> keysToRemove = new ArrayList<>(this.data.keySet());
        for (ConfigNode<?> childrenNodes : this.parentNode.getChildren()) {
            keysToRemove.remove(childrenNodes.getKey());
        }
        for (String key : keysToRemove) {
            this.data.remove(key);
        }
    }

    public <T> T getData(String key) {
        if (!this.data.containsKey(key))
            return null;

        ConfigNode<?> currentNode = this.data.get(key);
        return (T) currentNode.getValue();
    }

    /*
        Create a new ConfigNode with the passed in defaultConfig object. This config node assumes the position of this
        ExpandableConfig's node and will load its data fresh, invalidating the setData method. Upon save, the data hashmap
        will be wiped and replaced with the values of this new node. This is never how I imagined the config system being
        used, but this is a powerful feature.
     */
    public <T extends Config> T buildConfig(T defaultConfig) {
        if(this.syncedNode != null)
            throw new IllegalStateException("Synced config node already created for key \"" + this.key + "\".");

        ConfigConverter converter = ConfigConverter.getConverter(defaultConfig.getClass());
        if (converter == null)
            return defaultConfig;
        try {
            IFieldData fieldData = new ConfigConverter.FieldData(null, defaultConfig.getClass(), defaultConfig.getClass());
            this.syncedNode = converter.deserializeNodeWithFieldAndObject(this.parentNode.getParent(), this.parentNode.getKey(), defaultConfig, fieldData);
        } catch (Exception ex) {
            ElevatorsAPI.log(Level.SEVERE, "Failed to recreate config for hook \"" + this.getKey() + "\".  Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(ex));
        }

        return defaultConfig;
    }

    public <T extends Config> T getSyncedConfig(T defaultConfig) {
        if(this.syncedNode == null)
            return this.buildConfig(defaultConfig);

        return (T) this.syncedNode.getValue();
    }

    public String getKey() {
        return this.key;
    }

    @Override()
    public final void setKey(String key) {
        this.key = key;
    }

    @Override()
    public final void onSave() {
        if(this.syncedNode == null)
            return;

        this.clearData();
        for (ConfigNode<?> childNode : this.syncedNode.getChildren()) {
            // We actually don't pass the comments, as the ConfigConverter from earlier sets them.
            this.setData(childNode.getKey(), childNode.getValue(), null);
        }

    }

}
