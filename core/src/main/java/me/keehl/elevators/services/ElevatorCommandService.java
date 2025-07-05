package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.services.commands.ElevatorCommand;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ElevatorCommandService {

    private static boolean initialized = false;

    public static void init() {
        if(ElevatorCommandService.initialized)
            return;
        Elevators.pushAndHoldLog();

        JavaPlugin plugin = Elevators.getInstance();

        ElevatorCommand commands = new ElevatorCommand();
        Objects.requireNonNull(plugin.getCommand("elevators")).setExecutor(commands);
        Objects.requireNonNull(plugin.getCommand("elevators")).setTabCompleter(commands);

        ElevatorCommandService.initialized = true;
        Elevators.popLog(logData -> Elevators.log("Command service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

}
