package me.keehl.elevators.api.services.configs.versions;

import me.keehl.elevators.api.util.config.Config;

public interface IConfigEffect extends Config {

    String getFile();

    int getScale();

    float getDuration();

    boolean getUseHolo();

    String getBackground();

}
