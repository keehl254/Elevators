package com.lkeehl.elevators.services.configs;

import com.lkeehl.elevators.util.config.Config;

public class ConfigLocale implements Config {

    private final transient String prefix = "&c&lELEVATORS&f ";

    public String cantCreateMessage = prefix + "You do not have permission to create an Elevator!";

    public String cantDyeMessage = prefix + "You do not have permission to dye this Elevator!";

    public String cantUseMessage = prefix + "You do not have permission to use an Elevator!";

    public String cantGiveMessage = prefix + "You do not have permission to give Elevators!";

    public String cantAdministrateMessage = prefix + "You do not have permission to administrate Elevators!";

    public String cantReloadMessage = prefix + "You do not have permission to reload Elevators!";

    public String notEnoughRoomGiveMessage = prefix + "You do not have enough space in your inventory! The Elevator is on the ground in front of you!";

    public String givenElevatorMessage = prefix + "You have been given an Elevator!";

    public String worldDisabledMessage = prefix + "Elevators have been disabled in this world!";

    public String elevatorNowProtected = prefix + "The elevator can now only be used by trusted players.";

    public String elevatorNowUnprotected = prefix + "The elevator can now be used by anyone.";



}
