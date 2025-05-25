package me.keehl.elevators.util.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedHashMap;
import java.util.Map;

public class ElevatorsRepresenter extends Representer {

    /**
     * @deprecated options required
     */
    @Deprecated
    public ElevatorsRepresenter() {
        this(new DumperOptions());
    }

    public ElevatorsRepresenter(@NotNull DumperOptions options) {
        super(options);
        this.multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
        this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
        this.multiRepresenters.remove(Enum.class);
    }

    // SPIGOT-6949: Used by configuration sections that are nested within lists or maps.
    private class RepresentConfigurationSection extends RepresentMap {

        @NotNull
        @Override
        public Node representData(@NotNull Object data) {
            return super.representData(((ConfigurationSection) data).getValues(false));
        }
    }

    private class RepresentConfigurationSerializable extends RepresentMap {

        @NotNull
        @Override
        public Node representData(@NotNull Object data) {
            ConfigurationSerializable serializable = (ConfigurationSerializable) data;
            Map<String, Object> values = new LinkedHashMap<>();
            values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
            values.putAll(serializable.serialize());

            return super.representData(values);
        }
    }
}
