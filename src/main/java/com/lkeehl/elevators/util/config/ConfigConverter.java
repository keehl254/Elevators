package com.lkeehl.elevators.util.config;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ResourceHelper;
import com.lkeehl.elevators.util.config.converter.*;
import com.lkeehl.elevators.util.config.nodes.ClassicConfigNode;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;
import com.lkeehl.elevators.util.config.nodes.ConfigRootNode;
import com.lkeehl.elevators.util.config.nodes.DirectConfigNode;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.representer.Representer;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class ConfigConverter {

    private static final LinkedHashSet<ConfigConverter> converters = new LinkedHashSet<>();

    private static Yaml yaml;

    static {
        try {
            addConverter(PrimitiveConfigConverter.class);
            addConverter(ConfigConfigConverter.class);
            addConverter(ListConfigConverter.class);
            addConverter(MapConfigConverter.class);
            addConverter(ArrayConfigConverter.class);
            addConverter(SetConfigConverter.class);
            addConverter(EnumConfigConverter.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        DumperOptions yamlOptions = new DumperOptions();
        yamlOptions.setIndent(2);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer yamlRepresenter = new YamlRepresenter(yamlOptions);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setPropertyUtils(new PropertyUtils() {
            @Override
            protected Set<Property> createPropertySet(Class<?> type, BeanAccess bAccess) {
                return getPropertiesMap(type, bAccess).values().stream().filter(prop -> prop.isReadable() && (isAllowReadOnlyProperties() || prop.isWritable())).collect(Collectors.toCollection(LinkedHashSet::new));
            }
        });

        ConfigConverter.yaml = new Yaml(new CustomClassLoaderConstructor(Elevators.getInstance().getClass().getClassLoader(), new LoaderOptions()), yamlRepresenter, yamlOptions);
    }

    public static void addConverter(Class<? extends ConfigConverter> clazz) throws Exception {
        if (!ConfigConverter.class.isAssignableFrom(clazz))
            throw new Exception("Converter does not implement the Interface Converter");

        try {
            converters.add(clazz.getConstructor().newInstance());
        } catch (NoSuchMethodException e) {
            throw new Exception("Converter does not implement an accessible Constructor", e);
        } catch (InvocationTargetException e) {
            throw new Exception("Converter could not be invoked", e);
        } catch (InstantiationException e) {
            throw new Exception("Converter could not be instantiated", e);
        } catch (IllegalAccessException e) {
            throw new Exception("Converter does not implement a public Constructor which takes the InternalConverter instance", e);
        }
    }

    public static <T extends Config> ConfigRootNode<T> createNodeForConfigData(T config, Map<?, ?> yamlData) throws Exception {
        Optional<ConfigConverter> optConverter = converters.stream().filter(i -> i.supports(config.getClass())).findAny();
        if (optConverter.isEmpty())
            return null;

        ConfigConverter converter = optConverter.get();
        if (converter instanceof ConfigConfigConverter ccc) {
            ConfigRootNode<T> root = new ConfigRootNode<>(yamlData == null ? new HashMap<>() : yamlData, config);
            config.setKey("root");
            ccc.constructMapToConfig(root, root, config, config.getClass());
            config.onLoad();
            return root;
        }
        return null;
    }

    public static <T extends Config> ConfigRootNode<T> createNodeForConfig(T config, InputStream inputStream) throws Exception {
        Map<?, ?> yamlData;
        try (InputStreamReader fileReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            yamlData = yaml.load(fileReader);
        } catch (IOException | ClassCastException | YAMLException e) {
            throw new Exception("Could not load YML", e);
        }
        return createNodeForConfigData(config, yamlData);
    }

    public static <T extends Config> ConfigRootNode<T> createNodeForConfig(T config, File file) throws Exception {
        return createNodeForConfig(config, new FileInputStream(file));
    }

    public static boolean saveConfigToFile(ConfigRootNode<?> node, File file) {
        try (OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {

            int depth = 0;
            ArrayList<String> keyChain = new ArrayList<>();
            String yamlString = yaml.dump(node.serializeToObject());
            StringBuilder writeLines = new StringBuilder();
            for (String line : yamlString.split("\n")) {
                if (line.startsWith(new String(new char[depth]).replace("\0", " "))) {
                    keyChain.add(line.split(":")[0].trim());
                    depth = depth + 2;
                } else {
                    if (line.startsWith(new String(new char[depth - 2]).replace("\0", " ")))
                        keyChain.removeLast();
                    else {
                        //Check how much spaces are infront of the line
                        int spaces = 0;
                        for (int i = 0; i < line.length(); i++) {
                            if (line.charAt(i) == ' ')
                                spaces++;
                            else
                                break;
                        }

                        depth = spaces;

                        if (spaces == 0) {
                            keyChain = new ArrayList<>();
                            depth = 2;
                        } else {
                            ArrayList<String> temp = new ArrayList<>();
                            int index = 0;
                            for (int i = 0; i < spaces; i = i + 2, index++)
                                temp.add(keyChain.get(index));

                            keyChain = temp;

                            depth = depth + 2;
                        }
                    }

                    keyChain.add(line.split(":")[0].trim());
                }

                String search = join(keyChain);
                for (String comment : node.getCommentsAtPath(search)) {
                    writeLines.append(new String(new char[depth - 2]).replace("\0", " "));
                    writeLines.append("# ");
                    writeLines.append(comment);
                    writeLines.append("\n");
                }

                writeLines.append(line);
                writeLines.append("\n");
            }

            fileWriter.write(writeLines.toString());
            return true;
        } catch (IOException e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Failed while saving config. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
            return false;
        }
    }

    private static String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first)
                first = false;
            else
                sb.append(".");
            sb.append(item);
        }

        return sb.toString();
    }

    public static ConfigConverter getConverter(Class<?> type) {
        for (ConfigConverter converter : converters) {
            if (converter.supports(type))
                return converter;
        }
        Elevators.getElevatorsLogger().warning("Failed to find config converter for type: " + type.getName());
        return null;
    }

    // public abstract Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception;

    public abstract ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception;

    public abstract Object createObjectFromNode(ConfigNode<?> node) throws Exception;

    public abstract Object createObjectFromValue(Object value) throws Exception;

    public abstract boolean supports(Class<?> type);

    public abstract String getFieldDisplay(ConfigNode<?> node);

    public ConfigNode<?> createNodeWithData(ConfigNode<?> parentNode, String key, Object object, @Nullable Field field) {
        if (field == null)
            return new DirectConfigNode<>(parentNode, key, object);
        return new ClassicConfigNode<>(parentNode, field, object);
    }

}
