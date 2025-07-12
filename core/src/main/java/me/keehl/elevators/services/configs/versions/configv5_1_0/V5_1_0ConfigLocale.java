package me.keehl.elevators.services.configs.versions.configv5_1_0;

import me.keehl.elevators.util.config.Config;

public class V5_1_0ConfigLocale implements Config {

    private final transient String prefix = "&c&lELEVATORS&f ";

    public String cantCreateMessage = this.prefix + "You do not have permission to create an Elevator!";

    public String cantDyeMessage = this.prefix + "You do not have permission to dye this Elevator!";

    public String cantUseMessage = this.prefix + "You do not have permission to use an Elevator!";

    public String cantGiveMessage = this.prefix + "You do not have permission to give Elevators!";

    public String cantAdministrateMessage = this.prefix + "You do not have permission to administrate Elevators!";

    public String cantReloadMessage = this.prefix + "You do not have permission to reload Elevators!";

    public String notEnoughRoomGiveMessage = this.prefix + "You do not have enough space in your inventory! The Elevator is on the ground in front of you!";

    public String givenElevatorMessage = this.prefix + "You have been given an Elevator!";

    public String worldDisabledMessage = this.prefix + "Elevators have been disabled in this world!";

    public String elevatorChangedKickedOut = this.prefix + "The elevator has been changed or no longer exists. Leaving menu...";

    public String chatInputBackOut = "&7Type \"cancel\" to back out of chat input.";

    public String chatInputBackOutAllowReset = "&7Type \"cancel\" to back out of chat input or type \"reset\" to reset.";

    public String enterDisplayName = this.prefix + "Enter a new display name into the chat.";

    public String enterRecipeName = this.prefix + "Enter a new recipe name into the chat.";

    public String enterRecipePermission = this.prefix + "Enter the new recipe permission node into the chat.";

    public String enterFloorName = this.prefix + "Enter a new floor name into the chat.";

    public String enterTitle = this.prefix + "Enter a new title into the chat.";

    public String enterSubtitle = this.prefix + "Enter a new subtitle into the chat.";

    public String enterMessage = this.prefix + "Enter a new message into the chat.";

    public String enterElevatorKey = this.prefix + "Enter a new elevator key into the chat.";

    public String nonUniqueElevatorKey = this.prefix + "The elevator key must be unique.";

    public String nonUniqueRecipeName = this.prefix + "The elevator recipe key must be unique for this elevator type.";

    public String enterCommand = this.prefix + "Enter a new command into the chat.";



}
