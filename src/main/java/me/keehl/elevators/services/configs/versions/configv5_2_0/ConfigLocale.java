package me.keehl.elevators.services.configs.versions.configv5_2_0;

import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.services.configs.versions.IConfigLocale;
import me.keehl.elevators.helpers.MessageHelper;

public class ConfigLocale implements IConfigLocale {

    private final transient String prefix = "&c&lELEVATORS&f ";

    public ILocaleComponent cantCreateMessage = MessageHelper.getLocaleComponent(this.prefix + "You do not have permission to create an Elevator!");

    public ILocaleComponent cantDyeMessage = MessageHelper.getLocaleComponent(this.prefix + "You do not have permission to dye this Elevator!");

    public ILocaleComponent cantUseMessage = MessageHelper.getLocaleComponent(this.prefix + "You do not have permission to use an Elevator!");

    public ILocaleComponent cantGiveMessage = MessageHelper.getLocaleComponent(this.prefix + "You do not have permission to give Elevators!");

    public ILocaleComponent cantAdministrateMessage = MessageHelper.getLocaleComponent(this.prefix + "You do not have permission to administrate Elevators!");

    public ILocaleComponent cantReloadMessage = MessageHelper.getLocaleComponent(this.prefix + "You do not have permission to reload Elevators!");

    public ILocaleComponent notEnoughRoomGiveMessage = MessageHelper.getLocaleComponent(this.prefix + "You do not have enough space in your inventory! The Elevator is on the ground in front of you!");

    public ILocaleComponent givenElevatorMessage = MessageHelper.getLocaleComponent(this.prefix + "You have been given an Elevator!");

    public ILocaleComponent worldDisabledMessage = MessageHelper.getLocaleComponent(this.prefix + "Elevators have been disabled in this world!");

    public ILocaleComponent elevatorChangedKickedOut = MessageHelper.getLocaleComponent(this.prefix + "The elevator has been changed or no longer exists. Leaving menu...");

    public ILocaleComponent chatInputBackOut = MessageHelper.getLocaleComponent("&7Type \"cancel\" to back out of chat input.");

    public ILocaleComponent chatInputBackOutAllowReset = MessageHelper.getLocaleComponent("&7Type \"cancel\" to back out of chat input or type \"reset\" to reset.");

    public ILocaleComponent enterDisplayName = MessageHelper.getLocaleComponent(this.prefix + "Enter a new display name into the chat.");

    public ILocaleComponent enterRecipeName = MessageHelper.getLocaleComponent(this.prefix + "Enter a new recipe name into the chat.");

    public ILocaleComponent enterRecipePermission = MessageHelper.getLocaleComponent(this.prefix + "Enter the new recipe permission node into the chat.");

    public ILocaleComponent enterUsePermission = MessageHelper.getLocaleComponent(this.prefix + "Enter the new use permission node into the chat.");

    public ILocaleComponent enterDyePermission = MessageHelper.getLocaleComponent(this.prefix + "Enter the new dye permission node into the chat.");

    public ILocaleComponent enterFloorName = MessageHelper.getLocaleComponent(this.prefix + "Enter a new floor name into the chat.");

    public ILocaleComponent enterTitle = MessageHelper.getLocaleComponent(this.prefix + "Enter a new title into the chat.");

    public ILocaleComponent enterSubtitle = MessageHelper.getLocaleComponent(this.prefix + "Enter a new subtitle into the chat.");

    public ILocaleComponent enterMessage = MessageHelper.getLocaleComponent(this.prefix + "Enter a new message into the chat.");

    public ILocaleComponent enterElevatorKey = MessageHelper.getLocaleComponent(this.prefix + "Enter a new elevator key into the chat.");

    public ILocaleComponent nonUniqueElevatorKey = MessageHelper.getLocaleComponent(this.prefix + "The elevator key must be unique.");

    public ILocaleComponent nonUniqueRecipeName = MessageHelper.getLocaleComponent(this.prefix + "The elevator recipe key must be unique for this elevator type.");

    public ILocaleComponent enterCommand = MessageHelper.getLocaleComponent(this.prefix + "Enter a new command into the chat.");


    @Override
    public ILocaleComponent getCantCreateMessage() {
        return this.cantCreateMessage;
    }

    @Override
    public ILocaleComponent getCantDyeMessage() {
        return this.cantDyeMessage;
    }

    @Override
    public ILocaleComponent getCantUseMessage() {
        return this.cantUseMessage;
    }

    @Override
    public ILocaleComponent getCantGiveMessage() {
        return this.cantGiveMessage;
    }

    @Override
    public ILocaleComponent getCantAdministrateMessage() {
        return this.cantAdministrateMessage;
    }

    @Override
    public ILocaleComponent getCantReloadMessage() {
        return this.cantReloadMessage;
    }

    @Override
    public ILocaleComponent getNotEnoughRoomGiveMessage() {
        return this.notEnoughRoomGiveMessage;
    }

    @Override
    public ILocaleComponent getGivenElevatorMessage() {
        return this.givenElevatorMessage;
    }

    @Override
    public ILocaleComponent getWorldDisabledMessage() {
        return this.worldDisabledMessage;
    }

    @Override
    public ILocaleComponent getElevatorChangedKickedOutMessage() {
        return this.elevatorChangedKickedOut;
    }

    @Override
    public ILocaleComponent getChatInputBackOutMessage() {
        return this.chatInputBackOut;
    }

    @Override
    public ILocaleComponent getChatInputBackOutAllowResetMessage() {
        return this.chatInputBackOutAllowReset;
    }

    @Override
    public ILocaleComponent getEnterDisplayNameMessage() {
        return this.enterDisplayName;
    }

    @Override
    public ILocaleComponent getEnterRecipeNameMessage() {
        return this.enterRecipeName;
    }

    @Override
    public ILocaleComponent getEnterRecipePermissionMessage() {
        return this.enterRecipePermission;
    }

    @Override
    public ILocaleComponent getEnterUsePermissionMessage() {
        return this.enterUsePermission;
    }

    @Override
    public ILocaleComponent getEnterDyePermissionMessage() {
        return this.enterDyePermission;
    }

    @Override
    public ILocaleComponent getEnterFloorNameMessage() {
        return this.enterFloorName;
    }

    @Override
    public ILocaleComponent getEnterTitleMessage() {
        return this.enterTitle;
    }

    @Override
    public ILocaleComponent getEnterSubtitleMessage() {
        return this.enterSubtitle;
    }

    @Override
    public ILocaleComponent getEnterMessageMessage() {
        return this.enterMessage;
    }

    @Override
    public ILocaleComponent getEnterElevatorKeyMessage() {
        return this.enterElevatorKey;
    }

    @Override
    public ILocaleComponent getNonUniqueElevatorKeyMessage() {
        return this.nonUniqueElevatorKey;
    }

    @Override
    public ILocaleComponent getNonUniqueRecipeNameMessage() {
        return this.nonUniqueRecipeName;
    }

    @Override
    public ILocaleComponent getEnterCommandMessage() {
        return this.enterCommand;
    }
}
