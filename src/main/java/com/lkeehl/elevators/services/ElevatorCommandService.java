package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.services.commands.ElevatorCommand;

import java.util.Objects;

public class ElevatorCommandService {

    private static boolean initialized = false;

    public static void init(Elevators elevators) {
        if(ElevatorCommandService.initialized)
            return;

        ElevatorCommand commands = new ElevatorCommand(elevators);
        Objects.requireNonNull(elevators.getCommand("elevators")).setExecutor(commands);
        Objects.requireNonNull(elevators.getCommand("elevators")).setTabCompleter(commands);

        ElevatorCommandService.initialized = true;
    }

}
