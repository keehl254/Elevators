package me.keehl.elevators.services.configs;

import me.keehl.elevators.api.util.config.Config;

/*
    This class is used to upgrade from a current config type into a newer version.
    T is the currentVersion, Z is for the version this should upgrade into.

    If there is no later version than the current, then Z should be the same as T.
 */
public abstract class ConfigVersion<T extends Config, Z extends Config> {

    public abstract Z upgradeVersion(T currentConfig);

}
