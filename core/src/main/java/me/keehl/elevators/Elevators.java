package me.keehl.elevators;

import me.keehl.elevators.services.*;
import com.tcoded.folialib.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class Elevators {

    protected static JavaPlugin instance;
    protected static FoliaLib foliaLib;

    protected static boolean initialized = false;

    protected static void enable(JavaPlugin plugin, FoliaLib foliaLib) {
        instance = plugin;
        Elevators.foliaLib = foliaLib;

        Elevators.pushAndHoldLog();

        ElevatorDataContainerService.init();
        ElevatorSettingService.init();
        ElevatorVersionService.init();
        ElevatorEffectService.init();
        ElevatorActionService.init();
        ElevatorTypeService.init();
        ElevatorRecipeService.init();
        ElevatorObstructionService.init();
        ElevatorListenerService.init();
        ElevatorHookService.init();
        ElevatorHologramService.init();
        ElevatorCommandService.init();
        ElevatorUpdateService.init(plugin.getDescription().getVersion());

        Elevators.popLog(logData -> Elevators.log("Services enabled. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

        reloadElevators();
        initialized = true;
    }

    public static void disable() {

        Elevators.log("Disabling services");
        Elevators.pushLog();

        ElevatorHookService.unInitialize();
        ElevatorListenerService.unInitialize();
        ElevatorHologramService.onDisable();
        ElevatorUpdateService.unInitialize();

        saveConfig();
        initialized = false;

        Elevators.popLog();
    }

    public static void saveConfig() {
        File configFile = new File(instance.getDataFolder(), "config.yml");
        ElevatorConfigService.saveConfig(configFile);
    }

    public static void reloadElevators() {
        boolean alreadyLoadedBefore = ElevatorConfigService.isConfigLoaded();

        Elevators.pushAndHoldLog();

        File configFile = new File(instance.getDataFolder(), "config.yml");
        instance.saveDefaultConfig();

        ElevatorConfigService.loadConfig(configFile);
        saveConfig();

        Elevators.popLog(logData -> Elevators.log("Elevators " + (alreadyLoadedBefore ? "re" : "") + "loaded. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
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

    public static Logger getElevatorsLogger() {

        Plugin plugin = Bukkit.getPluginManager().getPlugin("Elevators");
        if (plugin != null)
            return plugin.getLogger();

        return Bukkit.getLogger();

    }

    private static final LogStack mainLogStack = new LogStack();

    public static void log(String message) {
        message = mainLogStack.log(message);
        if (message == null)
            return;

        Elevators.getElevatorsLogger().info(message);
    }

    public static void pushLog() {
        mainLogStack.push();
    }

    public static void popLog(Consumer<LogReleaseData> onPop) {
        LogReleaseData releaseData = mainLogStack.pop();
        if (onPop != null)
            onPop.accept(releaseData);
        for (String message : releaseData.getLogs())
            Elevators.log(message);

    }

    public static void popLog() {
        popLog(null);
    }

    public static void holdLog() {
        mainLogStack.holdLogs();
    }

    public static void pushAndHoldLog() {
        pushLog();
        holdLog();
    }

    public static void releaseLog(Consumer<LogReleaseData> onRelease) {
        LogReleaseData released = mainLogStack.releaseLogs();
        if (onRelease != null)
            onRelease.accept(released);
        for (String message : released.getLogs())
            Elevators.log(message);
    }

    public static void releaseLog() {
        releaseLog(null);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static class LogReleaseData {

        private final long elapsed;
        private final List<String> messages;

        protected LogReleaseData(long elapsed, List<String> messages) {
            this.elapsed = elapsed;
            this.messages = messages;
        }

        public long getElapsedTime() {
            return this.elapsed;
        }

        public List<String> getLogs() {
            return this.messages;
        }
    }

    public static class LogStack {

        private List<String> heldMessages;
        private LogStack child;
        private long holdStart = -1;

        public String log(String message) {

            if (this.child != null) {
                String returnMessage = this.child.log(message);
                if (returnMessage == null)
                    return null;

                returnMessage = "\t" + returnMessage;
                if (this.heldMessages != null) {
                    this.heldMessages.add(returnMessage);
                    return null;
                }
                return returnMessage;
            }

            if (this.heldMessages != null) {
                this.heldMessages.add(message);
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

            List<String> logs = new ArrayList<>();
            if (this.heldMessages != null) {
                for (String heldMessage : this.heldMessages)
                    logs.add("\t" + heldMessage);
            }
            this.heldMessages = null;
            long elapsed = System.currentTimeMillis() - (this.holdStart == -1 ? System.currentTimeMillis() : this.holdStart);
            return new LogReleaseData(elapsed, logs);
        }

    }
}
