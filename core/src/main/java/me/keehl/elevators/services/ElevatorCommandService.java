package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.services.commands.ElevatorCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ElevatorCommandService {

    private static boolean initialized = false;

    public static void init() {
        if(ElevatorCommandService.initialized)
            return;

        JavaPlugin plugin = Elevators.getInstance();

        ElevatorCommand commands = new ElevatorCommand();
        Objects.requireNonNull(plugin.getCommand("elevators")).setExecutor(commands);
        Objects.requireNonNull(plugin.getCommand("elevators")).setTabCompleter(commands);

        ElevatorCommandService.initialized = true;
    }

}
