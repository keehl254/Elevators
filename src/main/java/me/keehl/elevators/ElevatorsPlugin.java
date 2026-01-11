package me.keehl.elevators;

import com.tcoded.folialib.FoliaLib;
import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.Metrics;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.IElevatorsPlugin;
import me.keehl.elevators.helpers.ElevatorMenuHelper;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.helpers.VersionHelper;
import me.keehl.elevators.services.ElevatorsStartupService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ElevatorsPlugin extends JavaPlugin implements IElevatorsPlugin {

    private static final String LIGHT_GRAY = "\u001B[38;5;250m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    private org.bstats.bukkit.Metrics bstatsMetrics;
    private Metrics fastStatsMetrics;
    private final FoliaLib foliaLib = new FoliaLib(this);

    private CustomLogger customLogger;

    private Elevators elevators;

    private void printBanner() {

        String version = "Version: " + this.getDescription().getVersion();
        String server = this.foliaLib.getImplType().name() + " " + Bukkit.getServer().getVersion();
        String java = "Java Version:" + System.getProperty("java.version");
        String operatingSystem = "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version");

        Bukkit.getLogger().info("\n" +
                RED + "  _____ _               _                  \n" +
                RED + " | ____| | _____   ___ | |_ ___  _ __ ___  " + BOLD + LIGHT_GRAY + "    " + version + "\n" +
                RED + " |  _| | |/ _ \\ \\ / / \\| __/ _ \\| '__/ __| " + BOLD + LIGHT_GRAY + "    " + server + "\n" +
                RED + " | |___| |  __/\\ V / Λ \\ || (_) | |  \\__ \\ " + BOLD + LIGHT_GRAY + "    " + java + "\n" +
                RED + " |_____|_|\\___| \\_/_/ \\_\\__\\___/|_|  |___/ " + BOLD + LIGHT_GRAY + "    " + operatingSystem + "\n" +
                RED + "                                            " + RESET);
    }

    @Override
    public void onLoad() {
        this.elevators = new Elevators(this, this.foliaLib);
        Bukkit.getServicesManager().register(IElevators.class, this.elevators, this, ServicePriority.Highest);
        ElevatorsStartupService.buildElevatorsEarly(this, this.foliaLib);
    }

    @Override()
    public void onEnable() {
        this.getLogger().setFilter(new ElevatorLoggingFilter(this.getLogger().getFilter()));
        this.customLogger = new CustomLogger(this.getLogger());

        this.printBanner();

        ElevatorsAPI.pushAndHoldLog();

        ElevatorsAPI.pushAndHoldLog();
        try {
            this.fastStatsMetrics = BukkitMetrics.factory().token("ac50ca9cdff9c38b8a7aeea15b63ded6").create(this);
        } catch (Exception ex) {
            ElevatorsAPI.log(Level.WARNING, "Failed to load FastStats:\n" + ResourceHelper.cleanTrace(ex));
        }
        try {
            this.bstatsMetrics = new org.bstats.bukkit.Metrics(this, 8026);
        } catch (Exception ex) {
            ElevatorsAPI.log(Level.WARNING, "Failed to load BStats:\n" + ResourceHelper.cleanTrace(ex));
        }
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Metrics enabled. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

        if (VersionHelper.doesVersionSupportDialogs()) {
            ElevatorMenuHelper.registerDialogManager();
        }

        this.elevators.enable();
        ElevatorsStartupService.buildElevators(this, this.foliaLib);
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Plugin enabled. " + ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

    }

    @Override()
    public void onDisable() {
        if (this.bstatsMetrics != null) {
            this.getLogger().info("Disabling BStats Metrics");
            this.bstatsMetrics.shutdown();
        }
        if (this.fastStatsMetrics != null) {
            this.getLogger().info("Disabling FastStats Metrics");
            this.fastStatsMetrics.shutdown();
        }

        ElevatorMenuHelper.unregisterDialogManager();

        this.elevators.disable();
    }

    @Override()
    public @NotNull Logger getLogger() {
        if (this.customLogger == null)
            return super.getLogger();

        return this.customLogger;
    }

    @Override
    public void log(Object message) {
        if(message == null) message = "";
        super.getLogger().log(Level.INFO, message.toString());
    }

    @Override
    public void log(Level level, Object message) {
        if(message == null) message = "";
        super.getLogger().log(level, message.toString());
    }

    @Override
    public void log(Level level, Object message, Throwable throwable) {
        if(message == null) message = "";
        super.getLogger().log(level, message.toString(), throwable);
    }

    public static class ElevatorLoggingFilter implements Filter {

        private final Filter delegate;

        public ElevatorLoggingFilter(Filter delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean isLoggable(LogRecord record) {
            record.setLoggerName(BOLD + RED + "Elevators" + RESET);

            String message;
            if (record.getLevel() == Level.WARNING)
                message = YELLOW + record.getMessage();
            else if (record.getLevel() == Level.SEVERE)
                message = RED + record.getMessage();
            else
                message = LIGHT_GRAY + record.getMessage();

            message = message.replace("§e", YELLOW);
            record.setMessage(message + RESET);

            return this.delegate == null || this.delegate.isLoggable(record);
        }
    }


}
