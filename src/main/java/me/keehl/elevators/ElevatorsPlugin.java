package me.keehl.elevators;

import me.keehl.elevators.util.bstats.bukkit.Metrics;
import me.keehl.elevators.util.folialib.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ElevatorsPlugin extends JavaPlugin {

    private static final String LIGHT_GRAY = "\u001B[38;5;250m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    private Metrics metrics;

    private void printBanner(FoliaLib foliaLib) {

        String version = "Version: " + this.getDescription().getVersion();
        String server = foliaLib.getImplType().name() + " " +Bukkit.getServer().getVersion();
        String java = "Java Version:" + System.getProperty("java.version");
        String operatingSystem = "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version");

        Bukkit.getLogger().info("\n"+
        RED + "  _____ _               _                  \n"+
        RED + " | ____| | _____   ___ | |_ ___  _ __ ___  "+ BOLD+LIGHT_GRAY + "    " + version+"\n" +
        RED + " |  _| | |/ _ \\ \\ / / \\| __/ _ \\| '__/ __| "+ BOLD+LIGHT_GRAY + "    " + server+"\n" +
        RED + " | |___| |  __/\\ V / Λ \\ || (_) | |  \\__ \\ "+ BOLD+LIGHT_GRAY + "    " + java + "\n" +
        RED + " |_____|_|\\___| \\_/_/ \\_\\__\\___/|_|  |___/ "+ BOLD+LIGHT_GRAY + "    " + operatingSystem + "\n" +
        RED + "                                            " + RESET);
    }

    @Override()
    public void onEnable() {
        FoliaLib foliaLib = new FoliaLib(this);
        this.getLogger().setFilter(new ElevatorLoggingFilter(this.getLogger().getFilter()));

        this.printBanner(foliaLib);
        Elevators.pushAndHoldLog();

        Elevators.pushAndHoldLog();
        this.metrics = new Metrics(this, 8026);
        Elevators.popLog(logData -> Elevators.log("Metrics enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

        Elevators.enable(this, foliaLib);

        ElevatorHooks.buildHooks();
        Elevators.popLog(logData -> Elevators.log("Plugin enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

    }

    @Override()
    public void onDisable() {
        if(this.metrics != null) {
            this.getLogger().info("Disabling metrics");
            this.metrics.shutdown();
        }

        Elevators.disable();
    }

    public static class ElevatorLoggingFilter implements Filter {

        private final Filter delegate;

        public ElevatorLoggingFilter(Filter delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean isLoggable(LogRecord record) {
            record.setLoggerName(BOLD + RED + "Elevators" + RESET);

            String message = record.getMessage();

            if(record.getLevel() == Level.INFO)
                message = LIGHT_GRAY + record.getMessage();

            message = message.replace("§e", YELLOW);
            record.setMessage(message + RESET);

            return this.delegate == null || this.delegate.isLoggable(record);
        }
    }


}
