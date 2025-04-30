package com.lkeehl.elevators.services;

public class ElevatorHologramService {

    private static boolean initialized = false;

    public static void init() {
        if(ElevatorHologramService.initialized)
            return;

        ElevatorHologramService.initialized = true;
    }

}
