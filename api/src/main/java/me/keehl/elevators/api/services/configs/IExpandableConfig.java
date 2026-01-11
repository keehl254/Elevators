package me.keehl.elevators.api.services.configs;

import me.keehl.elevators.api.util.config.Config;

import java.util.List;

public interface IExpandableConfig extends Config {

    <T> void setData(String key, T value, List<String> comments);

    <T> void setData(String key, T value);

    <T> T getData(String key);

}
