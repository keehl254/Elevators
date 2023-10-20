package com.lkeehl.elevators.util.config;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.util.config.converter.*;
import com.lkeehl.elevators.util.config.nodes.ClassicConfigNode;
import com.lkeehl.elevators.util.config.nodes.ConfigNode;
import com.lkeehl.elevators.util.config.nodes.ConfigRootNode;
import com.lkeehl.elevators.util.config.nodes.DirectConfigNode;
import org.eclipse.jdt.annotation.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        DumperOptions yamlOptions = new DumperOptions();
        yamlOptions.setIndent(2);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer yamlRepresenter = new Representer(yamlOptions);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        ConfigConverter.yaml = new Yaml(new CustomClassLoaderConstructor(Elevators.class.getClassLoader(), new LoaderOptions()), yamlRepresenter, yamlOptions);
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

    public static <T extends Config> ConfigRootNode<T> createNodeForConfig(T config, File file) throws Exception {
        Map<?, ?> yamlData;
        try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            yamlData = yaml.load(fileReader);
        } catch (IOException | ClassCastException | YAMLException e) {
            throw new Exception("Could not load YML", e);
        }
        if (yamlData == null)
            return null;

        Optional<ConfigConverter> optConverter = converters.stream().filter(i -> i.supports(config.getClass())).findAny();
        if (optConverter.isEmpty())
            return null;

        ConfigConverter converter = optConverter.get();
        if (converter instanceof ConfigConfigConverter ccc) {
            ConfigRootNode<T> root = new ConfigRootNode<>(yamlData, config);
            ccc.constructMapToConfig(root, root, config, config.getClass());
            return root;
        }
        return null;
    }

    public static void saveConfigToFile(ConfigRootNode<?> node, File file) {
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
                        keyChain.remove(keyChain.size() - 1);
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
        } catch (IOException e) {
            Elevators.getElevatorsLogger().warning("Could not save YAML!");
            e.printStackTrace();
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
        return null;
    }

    // public abstract Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception;

    public abstract ConfigNode<?> createNodeFromFieldAndObject(ConfigNode<?> parentNode, Class<?> fieldType, String key, Object object, @Nullable Field field) throws Exception;

    public abstract Object createObjectFromNode(ConfigNode<?> node) throws Exception;

    public abstract boolean supports(Class<?> type);

    public abstract String getFieldDisplay(ConfigNode<?> node);

    public ConfigNode<?> createNodeWithData(ConfigNode<?> parentNode, String key, Object object, @Nullable Field field) {
        if (field == null)
            return new DirectConfigNode<>(parentNode, key, object);
        return new ClassicConfigNode<>(parentNode, field, object);
    }

}
