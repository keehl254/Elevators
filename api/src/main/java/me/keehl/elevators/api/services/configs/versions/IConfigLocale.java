package me.keehl.elevators.api.services.configs.versions;

import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.util.config.Config;

public interface IConfigLocale extends Config {

    ILocaleComponent getCantCreateMessage();

    ILocaleComponent getCantDyeMessage();

    ILocaleComponent getCantUseMessage();

    ILocaleComponent getCantGiveMessage();

    ILocaleComponent getCantAdministrateMessage();

    ILocaleComponent getCantReloadMessage();

    ILocaleComponent getNotEnoughRoomGiveMessage();

    ILocaleComponent getGivenElevatorMessage();

    ILocaleComponent getWorldDisabledMessage();

    ILocaleComponent getElevatorChangedKickedOutMessage();

    ILocaleComponent getChatInputBackOutMessage();

    ILocaleComponent getChatInputBackOutAllowResetMessage();

    ILocaleComponent getEnterDisplayNameMessage();

    ILocaleComponent getEnterRecipeNameMessage();

    ILocaleComponent getEnterRecipePermissionMessage();

    ILocaleComponent getEnterUsePermissionMessage();

    ILocaleComponent getEnterDyePermissionMessage();

    ILocaleComponent getEnterFloorNameMessage();

    ILocaleComponent getEnterTitleMessage();

    ILocaleComponent getEnterSubtitleMessage();

    ILocaleComponent getEnterMessageMessage();

    ILocaleComponent getEnterElevatorKeyMessage();

    ILocaleComponent getNonUniqueElevatorKeyMessage();

    ILocaleComponent getNonUniqueRecipeNameMessage();

    ILocaleComponent getEnterCommandMessage();

}
