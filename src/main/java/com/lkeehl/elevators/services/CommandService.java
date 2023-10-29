package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.services.commands.ElevatorCommand;

import java.util.Objects;

public class CommandService {

    private static boolean initialized = false;

    public static void init(Elevators elevators) {
        if(CommandService.initialized)
            return;

        ElevatorCommand commands = new ElevatorCommand(elevators);
        Objects.requireNonNull(elevators.getCommand("elevators")).setExecutor(commands);
        Objects.requireNonNull(elevators.getCommand("elevators")).setTabCompleter(commands);

        CommandService.initialized = true;
    }

}
