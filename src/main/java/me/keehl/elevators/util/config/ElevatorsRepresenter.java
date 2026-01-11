package me.keehl.elevators.util.config;

import me.keehl.elevators.api.util.config.RecipeRow;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ElevatorsRepresenter extends Representer {

    @Deprecated
    public ElevatorsRepresenter() {
        this(new DumperOptions());
    }

    public ElevatorsRepresenter(@NotNull DumperOptions options) {
        super(options);
        this.multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
        this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
        this.multiRepresenters.put(List.class, new RepresentListAndRecipeRow());
        this.multiRepresenters.remove(Enum.class);
    }

    private class RepresentListAndRecipeRow implements Represent {

        @Override
        public Node representData(Object data) {
            if (data instanceof RecipeRow)
                return representSequence(getTag(data.getClass(), Tag.SEQ), (RecipeRow<?>) data, DumperOptions.FlowStyle.FLOW);
            else
                return representSequence(getTag(data.getClass(), Tag.SEQ), (List<?>) data, DumperOptions.FlowStyle.BLOCK);
        }
    }

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
