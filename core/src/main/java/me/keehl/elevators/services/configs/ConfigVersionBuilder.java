package me.keehl.elevators.services.configs;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.configs.versions.configv1.V1ConfigRoot;
import me.keehl.elevators.services.configs.versions.configv2.V2ConfigRoot;
import me.keehl.elevators.services.configs.versions.configv2.V2ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv3.V3ConfigRoot;
import me.keehl.elevators.services.configs.versions.configv3.V3ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv4.V4ConfigRoot;
import me.keehl.elevators.services.configs.versions.configv4.V4ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv4_0_2.V4_0_2ConfigRoot;
import me.keehl.elevators.services.configs.versions.configv4_0_2.V4_0_2ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv5.V5ConfigRoot;
import me.keehl.elevators.services.configs.versions.configv5.V5ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv5_1_0.V5_1_0ConfigRoot;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigRoot;
import me.keehl.elevators.services.configs.versions.configv5_1_0.V5_1_0ConfigVersion;
import me.keehl.elevators.services.configs.versions.configv5_2_0.V5_2_0ConfigVersion;
import me.keehl.elevators.util.config.Config;
import me.keehl.elevators.util.config.ConfigConverter;
import me.keehl.elevators.util.config.nodes.ConfigRootNode;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Level;

public class ConfigVersionBuilder {

    private static final V2ConfigVersion v2ConfigVersion = new V2ConfigVersion();
    private static final V3ConfigVersion v3ConfigVersion = new V3ConfigVersion();
    private static final V4ConfigVersion v4ConfigVersion = new V4ConfigVersion();
    private static final V4_0_2ConfigVersion v4_0_2ConfigVersion = new V4_0_2ConfigVersion();
    private static final V5ConfigVersion v5ConfigVersion = new V5ConfigVersion();
    private static final V5_1_0ConfigVersion v5_1_0ConfigVersion = new V5_1_0ConfigVersion();
    private static final V5_2_0ConfigVersion v5_2_0ConfigVersion = new V5_2_0ConfigVersion();

    @SuppressWarnings("unchecked")
    private static <Z extends Config, T extends Config, V extends ConfigVersion<Z, T>> T convert(V converter, File configFile, Z existingRoot) throws Exception {

        if(existingRoot == null) {
            Type[] arguments = ((ParameterizedType) converter.getClass().getGenericSuperclass()).getActualTypeArguments();
            Class<T> newClass = (Class<T>) Elevators.getInstance().getClass().getClassLoader().loadClass(arguments[1].getTypeName());
            T newRoot = newClass.getConstructor().newInstance();
            return ConfigConverter.createNodeForConfig(newRoot, configFile).getRoot().getConfig();
        }

        return converter.upgradeVersion(existingRoot);

    }

    public static ConfigRootNode<ConfigRoot> getConfig(File configFile) {

        File backupFile = null;
        try {
            backupFile = new File(configFile.getParent(), configFile.getName() + "." + System.currentTimeMillis() + ".backup");
            Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            String version = ConfigConverter.createNodeForConfig(new BlankRoot(), configFile).getConfig().version;

            Config root = null;

            switch (version) {
                case "1.0.0":
                    root = ConfigConverter.createNodeForConfig(new V1ConfigRoot(), configFile).getConfig();
                case "2.0.0":
                    root = convert(v2ConfigVersion, configFile, (V1ConfigRoot) root);
                case "3.0.0":
                    root = convert(v3ConfigVersion, configFile, (V2ConfigRoot) root);
                case "4.0.0":
                    root = convert(v4ConfigVersion, configFile, (V3ConfigRoot) root);
                case "4.0.2":
                    root = convert(v4_0_2ConfigVersion, configFile, (V4ConfigRoot) root);
                case "5.0.0":
                    root = convert(v5ConfigVersion, configFile, (V4_0_2ConfigRoot) root);
                case "5.1.0":
                    root = convert(v5_1_0ConfigVersion, configFile, (V5ConfigRoot) root);
                default:
                    root = convert(v5_2_0ConfigVersion, configFile, (V5_1_0ConfigRoot) root);
            }

            ConfigConverter converter = ConfigConverter.getConverter(root.getClass());
            if(converter == null)
                throw new RuntimeException("Failed to convert elevators version.");

            Map<?,?> data = (Map<?, ?>) converter.serializeValueToObject(root);
            ConfigRootNode<ConfigRoot> rootNode = ConfigConverter.createNodeForConfigData((ConfigRoot) root, data);
            if(rootNode == null)
                throw new RuntimeException("Failed to convert elevators version.");

            if(!ConfigConverter.saveConfigToFile(rootNode, configFile))
                throw new RuntimeException("Failed to save converted elevator version.");

            if(!backupFile.delete())
                Elevators.getElevatorsLogger().log(Level.WARNING, "Failed to delete backup config file.");

            return rootNode;

        } catch (Exception e) {
            Elevators.getElevatorsLogger().log(Level.SEVERE, "Error loading config. Using defaults. Please create an issue ticket on my GitHub with your config if you would like assistance: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }

        if (backupFile != null) {
            try {
                Files.copy(backupFile.toPath(), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                Elevators.getElevatorsLogger().log(Level.SEVERE, "Error reverting to old config. Config backup is available in the Elevators config path.");
            }
        }

        ElevatorConfigService.invalidateConfig();
        try {
            return ConfigConverter.createNodeForConfigData(new ConfigRoot(), null);
        } catch (Exception e) {
            return null;
        }
    }


}
