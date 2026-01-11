package me.keehl.elevators.util.config;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.util.config.Config;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.util.config.converter.*;
import me.keehl.elevators.util.config.nodes.ClassicConfigNode;
import me.keehl.elevators.util.config.nodes.ConfigNode;
import me.keehl.elevators.util.config.nodes.ConfigRootNode;
import me.keehl.elevators.util.config.nodes.DirectConfigNode;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.representer.Representer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class ConfigConverter {

    private static final LinkedHashSet<ConfigConverter> converters = new LinkedHashSet<>();
    private static final Map<Class<?>, Class<?>> remappedClasses = new HashMap<>();

    private static Yaml yaml;

    static {
        try {
            addConverter(PrimitiveConfigConverter.class);
            addConverter(ConfigConfigConverter.class);
            addConverter(ListConfigConverter.class);
            addConverter(MapConfigConverter.class);
            addConverter(ArrayConfigConverter.class);
            addConverter(SetConfigConverter.class);
            addConverter(MaterialConfigConverter.class);
            addConverter(EnumConfigConverter.class);
            addConverter(RecipeRowConfigConverter.class);
            addConverter(NamespacedKeyConfigConverter.class);
            addConverter(ComponentConfigConverter.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        DumperOptions yamlOptions = new DumperOptions();
        yamlOptions.setIndent(2);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlOptions.setSplitLines(false);

        Representer yamlRepresenter = new ElevatorsRepresenter(yamlOptions);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setPropertyUtils(new PropertyUtils() {
            @Override
            protected Set<Property> createPropertySet(Class<?> type, BeanAccess bAccess) {
                return getPropertiesMap(type, bAccess).values().stream().filter(prop -> prop.isReadable() && (isAllowReadOnlyProperties() || prop.isWritable())).collect(Collectors.toCollection(LinkedHashSet::new));
            }
        });

        ConfigConverter.yaml = new Yaml(new CustomClassLoaderConstructor(Elevators.getInstance().getClass().getClassLoader(), new LoaderOptions()), yamlRepresenter, yamlOptions);
    }

    public static void remapClass(Class<?> clazz1, Class<?> clazz2) {
        remappedClasses.put(clazz1, clazz2);
    }

    public static Class<?> getRemappedClass(Class<?> clazz1) {
        if(!remappedClasses.containsKey(clazz1))
            return clazz1;

        return remappedClasses.get(clazz1);
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
            ccc.constructMapToConfig(root, root, config, new FieldData(null, config.getClass(), config.getClass()));
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
        return createNodeForConfig(config, Files.newInputStream(file.toPath()));
    }

    public static boolean saveConfigToFile(ConfigRootNode<?> node, File file) {
        try (OutputStreamWriter fileWriter = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {

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
            ElevatorsAPI.log(Level.SEVERE, "Failed while saving config. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
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
        ElevatorsAPI.log(Level.WARNING, "Failed to find config converter for type: " + type.getName());
        return null;
    }

    // public abstract Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception;

    public abstract ConfigNode<?> deserializeNodeWithFieldAndObject(ConfigNode<?> parentNode, String key, Object object, FieldData fieldData) throws Exception;

    public abstract Object serializeNodeToObject(ConfigNode<?> node) throws Exception;

    public abstract Object serializeValueToYamlObject(Object value) throws Exception;

    public abstract boolean supports(Class<?> type);

    public abstract String getFieldDisplay(ConfigNode<?> node);

    public static ConfigNode<?> createNodeWithData(ConfigNode<?> parentNode, String key, Object object, @Nullable Field field) {
        if (field == null)
            return new DirectConfigNode<>(parentNode, key, object);
        return new ClassicConfigNode<>(parentNode, field, object);
    }

    public static class FieldData {

        private final Field field;
        private final Class<?> fieldClass;
        private final Type fieldType;

        public FieldData(Field field, Class<?> fieldClass, Type fieldType) {
            this.field = field;
            this.fieldClass = getRemappedClass(fieldClass);
            this.fieldType = fieldType;
        }

        public FieldData(@Nonnull Field field) {
            this.field = field;
            this.fieldClass = getRemappedClass(field.getType());
            this.fieldType = field.getGenericType();
        }


        public Field getField() {
            return this.field;
        }

        public Class<?> getFieldClass() {
            return this.fieldClass;
        }

        public Type getFieldType() {
            return this.fieldType;
        }

        private String getRawTypeName(Type type) {
            if (type instanceof ParameterizedType) {
                Type raw = ((ParameterizedType) type).getRawType();
                return raw.getTypeName();
            } else {
                return type.getTypeName();
            }
        }

        public FieldData[] getGenericData() throws ClassNotFoundException {

            // Generic array creation is not allowed in java, so no need to worry too much here
            if(this.fieldClass.isArray()) {
                Class<?> component = this.fieldClass.getComponentType();
                return new FieldData[] { new FieldData(null, component, component) };
            }
            ParameterizedType genericType;
            if(this.field != null) {

                // We know for sure it does not have a generic if it's not a ParameterizedType
                if(!(this.field.getGenericType() instanceof ParameterizedType))
                    return null;

                genericType = (ParameterizedType) this.field.getGenericType();
            } else if(this.fieldType instanceof ParameterizedType) {
                genericType = (ParameterizedType) this.fieldType;
            }else
                return null;

            List<FieldData> fieldDataList = new ArrayList<>();
            for(Type type : genericType.getActualTypeArguments()) {
                String typeName = getRawTypeName(type);
                Class<?> clazz = this.getClass().getClassLoader().loadClass(typeName);
                fieldDataList.add(new FieldData(null, clazz, type));
            }

            return fieldDataList.toArray(new FieldData[]{});
        }

    }

}
