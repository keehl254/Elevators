package me.keehl.elevators;

import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.IElevatorsPlugin;
import me.keehl.elevators.api.models.*;
import me.keehl.elevators.api.models.actions.IElevatorActionBuilder;
import me.keehl.elevators.api.models.hooks.IProtectionHook;
import me.keehl.elevators.api.models.settings.IElevatorSettingBuilder;
import me.keehl.elevators.api.services.*;
import me.keehl.elevators.api.services.configs.versions.*;
import me.keehl.elevators.api.util.logging.ILogMessage;
import me.keehl.elevators.api.util.logging.ILogReleaseData;
import me.keehl.elevators.api.util.persistantDataTypes.ElevatorsDataType;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.helpers.ShulkerBoxHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorRecipeGroup;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.actions.ElevatorActionBuilder;
import me.keehl.elevators.models.settings.ElevatorSettingBuilder;
import me.keehl.elevators.services.*;
import com.tcoded.folialib.FoliaLib;
import me.keehl.elevators.services.configs.versions.configv5_2_0.*;
import me.keehl.elevators.util.config.ConfigConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Elevators implements IElevators {

    protected static JavaPlugin instance;
    protected static FoliaLib foliaLib;

    protected static boolean initialized = false;

    private static ElevatorActionService actionService;
    private static ElevatorConfigService configService;
    private static ElevatorDataContainerService dataContainerService;
    private static ElevatorEffectService effectsService;
    private static ElevatorHologramService hologramService;
    private static ElevatorHookService hookService;
    private static ElevatorListenerService listenerService;
    private static ElevatorObstructionService obstructionService;
    private static ElevatorRecipeService recipeService;
    private static ElevatorSettingService settingService;
    private static ElevatorTypeService typeService;
    private static ElevatorUpdateService updateService;
    private static ElevatorVersionService versionService;

    /* For future me:
    It seems that the static values set onload are disposed of by the time onEnabled is called.
    The result is that "zip file closed" error when trying to access them...
     */
    public Elevators(JavaPlugin plugin, FoliaLib foliaLib) {
        Elevators.instance = plugin;
        Elevators.foliaLib = foliaLib;

        Bukkit.getServicesManager().register(IElevatorActionService.class, Elevators.actionService = new ElevatorActionService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorConfigService.class, Elevators.configService = new ElevatorConfigService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorDataContainerService.class, Elevators.dataContainerService = new ElevatorDataContainerService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorEffectsService.class, Elevators.effectsService = new ElevatorEffectService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorHologramService.class, Elevators.hologramService = new ElevatorHologramService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorHookService.class, Elevators.hookService = new ElevatorHookService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorListenerService.class, Elevators.listenerService = new ElevatorListenerService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorObstructionService.class, Elevators.obstructionService = new ElevatorObstructionService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorRecipeService.class, Elevators.recipeService = new ElevatorRecipeService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorSettingService.class, Elevators.settingService = new ElevatorSettingService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorTypeService.class, Elevators.typeService = new ElevatorTypeService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorUpdateService.class, Elevators.updateService = new ElevatorUpdateService(this), plugin, ServicePriority.Highest);
        Bukkit.getServicesManager().register(IElevatorVersionService.class, Elevators.versionService = new ElevatorVersionService(this), plugin, ServicePriority.Highest);

        ConfigConverter.remapClass(IConfigEffect.class, ConfigEffect.class);
        ConfigConverter.remapClass(IElevatorType.class, ElevatorType.class);
        ConfigConverter.remapClass(IConfigElevatorType.IConfigActions.class, ConfigElevatorType.ConfigActions.class);
        ConfigConverter.remapClass(IConfigHookData.class, ConfigHookData.class);
        ConfigConverter.remapClass(IConfigLocale.class, ConfigLocale.class);
        ConfigConverter.remapClass(IConfigRecipe.class, ConfigRecipe.class);
        ConfigConverter.remapClass(IConfigRoot.class, ConfigRoot.class);
        ConfigConverter.remapClass(IConfigSettings.class, ConfigSettings.class);
        ConfigConverter.remapClass(IElevatorRecipeGroup.class, ElevatorRecipeGroup.class);
    }

    protected void enable() {
        this.pushAndHoldLog();

        Elevators.actionService.onInitialize();
        Elevators.configService.onInitialize();
        Elevators.dataContainerService.onInitialize();
        Elevators.effectsService.onInitialize();
        Elevators.hologramService.onInitialize();
        Elevators.hookService.onInitialize();
        Elevators.listenerService.onInitialize();
        Elevators.obstructionService.onInitialize();
        Elevators.recipeService.onInitialize();
        Elevators.settingService.onInitialize();
        Elevators.typeService.onInitialize();
        Elevators.updateService.onInitialize();
        Elevators.versionService.onInitialize();

        this.popLog(logData -> this.log("Services enabled. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

        reloadElevators();
        initialized = true;
    }

    public void disable() {

        this.log("Disabling services");
        this.pushLog();

        Elevators.actionService.onUninitialize();
        Elevators.configService.onUninitialize();
        Elevators.dataContainerService.onUninitialize();
        Elevators.effectsService.onUninitialize();
        Elevators.hologramService.onUninitialize();
        Elevators.hookService.onUninitialize();
        Elevators.listenerService.onUninitialize();
        Elevators.obstructionService.onUninitialize();
        Elevators.recipeService.onUninitialize();
        Elevators.settingService.onUninitialize();
        Elevators.typeService.onUninitialize();
        Elevators.updateService.onUninitialize();
        Elevators.versionService.onUninitialize();

        saveConfig();
        initialized = false;

        this.popLog();
    }

    public void saveConfig() {
        File configFile = new File(instance.getDataFolder(), "config.yml");
        Elevators.configService.saveConfig(configFile);
    }

    public static IConfigLocale getLocale() {
        return Elevators.getConfigService().getRootConfig().getLocale();
    }

    public static void reloadElevators() {
        boolean alreadyLoadedBefore = Elevators.getConfigService().isConfigLoaded();

        ElevatorsAPI.pushAndHoldLog();

        File configFile = new File(instance.getDataFolder(), "config.yml");
        instance.saveDefaultConfig();

        Elevators.getConfigService().loadConfig(configFile);
        Elevators.getInstance().saveConfig();

        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Elevators " + (alreadyLoadedBefore ? "re" : "") + "loaded. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    @Override
    public JavaPlugin getPlugin() {
        return getInstance();
    }

    public static JavaPlugin getInstance() { // I consider it bad practice to rely on a static instance, so I am prioritizing using getPlugin.
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        if (plugin != null)
            return (JavaPlugin) plugin;
        return instance;
    }

    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public static File getConfigDirectory() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        File configDirectory;
        if (plugin == null) {
            plugin = Bukkit.getPluginManager().getPlugins()[0];
            configDirectory = new File(plugin.getDataFolder().getParent(), "Elevators");
        } else
            configDirectory = plugin.getDataFolder();
        try {
            //noinspection ResultOfMethodCallIgnored
            configDirectory.mkdirs();
        } catch (Exception ignored) {
        }

        return configDirectory;
    }

    @Override
    public <T> IElevatorSettingBuilder<T> settingsBuilder(String settingKey, T defaultValue, PersistentDataType<?, T> persistentDataType) {
        return new ElevatorSettingBuilder<>(settingKey, defaultValue, persistentDataType);
    }

    @Override
    public <T> IElevatorSettingBuilder<T> settingsBuilder(String settingKey, T defaultValue, ElevatorsDataType elevatorsDataType) {
        return new ElevatorSettingBuilder<>(settingKey, defaultValue, elevatorsDataType);
    }

    @Override
    public IElevatorActionBuilder actionBuilder(String actionKey) {
        return new ElevatorActionBuilder(actionKey);
    }

    @Override
    public IElevator createElevatorRecord(ShulkerBox shulkerBox, IElevatorType elevatorType) {
        return new Elevator(shulkerBox, elevatorType);
    }

    @Override
    public IElevator createElevatorRecord(Block block) {
        ShulkerBox box = ShulkerBoxHelper.getShulkerBox(block);
        if (box == null)
            return null;
        IElevatorType elevatorType = ElevatorsAPI.getElevatorType(box);
        if (elevatorType == null)
            return null;

        return this.createElevatorRecord(box, elevatorType);
    }

    @Override
    public IElevatorType getElevatorType(ShulkerBox box) {
        return ElevatorHelper.getElevatorType(box);
    }

    @Override
    public void toggleElevatorProtectionHook(IElevator elevator, IProtectionHook protectionHook) {
        NamespacedKey containerKey = Elevators.getDataContainerService().getKeyFromKey("protection-" + protectionHook.getConfigKey(), IElevatorDataContainerService.booleanPersistentDataType);

        boolean currentValue = protectionHook.isCheckEnabled(elevator);
        Elevators.getDataContainerService().setElevatorValue(elevator.getShulkerBox(), containerKey, !currentValue);
        elevator.getShulkerBox().update();
    }

    @Override
    public IConfigHookData getElevatorProtectionHookConfig(IProtectionHook protectionHook) {
        if (!Elevators.getConfigService().getRootConfig().getProtectionHooks().containsKey(protectionHook.getConfigKey()))
            Elevators.getConfigService().getRootConfig().getProtectionHooks().put(protectionHook.getConfigKey(), new ConfigHookData());
        return Elevators.getConfigService().getRootConfig().getProtectionHooks().get(protectionHook.getConfigKey());
    }

    @Override
    public boolean isElevatorProtectionHookCheckEnabled(IElevator elevator, IProtectionHook protectionHook) {
        NamespacedKey containerKey = Elevators.getDataContainerService().getKeyFromKey("protection-" + protectionHook.getConfigKey(), IElevatorDataContainerService.booleanPersistentDataType);
        return Elevators.getDataContainerService().getElevatorValue(elevator.getShulkerBox(), containerKey, getElevatorProtectionHookConfig(protectionHook).doesBlockNonMemberUseByDefault());
    }

    public static ElevatorActionService getActionService() {
        return Elevators.actionService;
    }

    public static ElevatorConfigService getConfigService() {
        return Elevators.configService;
    }

    public static ElevatorDataContainerService getDataContainerService() {
        return Elevators.dataContainerService;
    }

    public static ElevatorEffectService getEffectsService() {
        return Elevators.effectsService;
    }

    public static ElevatorHologramService getHologramService() {
        return Elevators.hologramService;
    }

    public static ElevatorHookService getHooksService() {
        return Elevators.hookService;
    }

    public static ElevatorListenerService getListenerService() {
        return Elevators.listenerService;
    }

    public static ElevatorRecipeService getRecipeService() {
        return Elevators.recipeService;
    }

    public static ElevatorObstructionService getObstructionService() {
        return Elevators.obstructionService;
    }

    public static ElevatorSettingService getSettingService() {
        return Elevators.settingService;
    }

    public static ElevatorTypeService getElevatorTypeService() {
        return Elevators.typeService;
    }

    public static ElevatorUpdateService getUpdateService() {
        return Elevators.updateService;
    }

    public static ElevatorVersionService getVersionService() {
        return Elevators.versionService;
    }

    public static Logger getElevatorsLogger() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        if (plugin != null)
            return plugin.getLogger();

        return Bukkit.getLogger();
    }

    public static boolean isInitialized() {
        return initialized;
    }

    private static final LogStack mainLogStack = new LogStack();

    @Override
    public void log(Object message) {
        this.log(Level.INFO, message);
    }

    @Override
    public void log(Level level, Object message) {
        this.log(level, message, null);
    }

    @Override
    public void log(Level level, Object message, Throwable throwable) {
        if (throwable != null && level == Level.SEVERE) {
            ((IElevatorsPlugin) Elevators.getInstance()).log(level, message, throwable);
            return;
        }
        message = mainLogStack.log(level, message.toString(), throwable);
        if (message == null)
            return;

        ((IElevatorsPlugin) Elevators.getInstance()).log(level, message, throwable);
    }

    @Override
    public Logger getLogger() {
        return ((IElevatorsPlugin) Elevators.getInstance()).getLogger();
    }

    @Override
    public ILocaleComponent createComponentFromText(String message) {
        return MessageHelper.getLocaleComponent(message);
    }

    @Override
    public void pushLog() {
        mainLogStack.push();
    }

    @Override
    public ILogReleaseData popLog(Consumer<ILogReleaseData> onPop) {
        LogReleaseData releaseData = mainLogStack.pop();
        if (onPop != null)
            onPop.accept(releaseData);
        for (ILogMessage message : releaseData.getLogs())
            this.log(message.getLevel(), message.getMessage(), message.getThrowable());
        return releaseData;
    }

    @Override
    public ILogReleaseData popLog() {
        return popLog(null);
    }

    @Override
    public void holdLog() {
        mainLogStack.holdLogs();
    }

    @Override
    public void pushAndHoldLog() {
        pushLog();
        holdLog();
    }

    @Override
    public ILogReleaseData releaseLog(Consumer<ILogReleaseData> onRelease) {
        ILogReleaseData released = mainLogStack.releaseLogs();
        if (onRelease != null)
            onRelease.accept(released);
        for (ILogMessage message : released.getLogs())
            this.log(message.getLevel(), message.getMessage(), message.getThrowable());
        return released;
    }

    @Override
    public ILogReleaseData releaseLog() {
        return releaseLog(null);
    }

    public record LogReleaseData(long elapsed, List<ILogMessage> messages) implements ILogReleaseData {

        public long getElapsedTime() {
            return this.elapsed;
        }

        public List<ILogMessage> getLogs() {
            return this.messages;
        }
    }

    public static class LogMessage implements ILogMessage {

        private String message;
        private final Throwable throwable;
        private final Level level;

        public LogMessage(Level level, String message, Throwable throwable) {
            this.message = message;
            this.throwable = throwable;
            this.level = level;
        }

        public String getMessage() {
            return this.message;
        }

        public Throwable getThrowable() {
            return this.throwable;
        }

        public Level getLevel() {
            return this.level;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class LogStack {

        private List<ILogMessage> heldMessages;
        private LogStack child;
        private long holdStart = -1;

        public String log(Level level, String message, Throwable throwable) {

            if (this.child != null) {
                String returnMessage = this.child.log(level, message, throwable);
                if (returnMessage == null)
                    return null;

                returnMessage = "\t" + returnMessage;
                if (this.heldMessages != null) {
                    this.heldMessages.add(new LogMessage(level, message, throwable));
                    return null;
                }
                return returnMessage;
            }

            if (this.heldMessages != null) {
                this.heldMessages.add(new LogMessage(level, message, throwable));
                return null;
            }

            return message;
        }

        public void push() {
            if (this.child != null) {
                this.child.push();
                return;
            }
            this.child = new LogStack();
        }

        public LogReleaseData pop() {
            if (this.child != null) {
                boolean noGrandChild = this.child.child == null;

                LogReleaseData logs = this.child.pop();

                // No grandchild, so we drop our child.
                if (noGrandChild)
                    this.child = null;

                return logs;
            }
            return this.releaseLogs();
        }

        public void holdLogs() {
            if (this.child != null) {
                this.child.holdLogs();
                return;
            }
            this.holdStart = System.currentTimeMillis();
            this.heldMessages = new ArrayList<>();
        }

        public LogReleaseData releaseLogs() {
            if (this.child != null) {
                LogReleaseData childLogs = this.child.releaseLogs();

                // We have released the child messages but not the parent's.
                if (this.heldMessages != null) {
                    this.heldMessages.addAll(childLogs.messages);
                    return null;
                }
                return childLogs;
            }

            long elapsed = System.currentTimeMillis() - (this.holdStart == -1 ? System.currentTimeMillis() : this.holdStart);
            if (this.heldMessages == null)
                return new LogReleaseData(elapsed, new ArrayList<>());

            for (ILogMessage heldMessage : this.heldMessages) {
                heldMessage.setMessage("\t" + heldMessage.getMessage());
            }
            LogReleaseData data = new LogReleaseData(elapsed, this.heldMessages);
            this.heldMessages = null;
            return data;
        }

    }
}
