package me.keehl.elevators.util.config;

import me.keehl.elevators.api.util.config.Config;

import java.util.HashMap;
import java.util.Map;

public class BlankConfig implements Config {

    public transient Map<String, Object> data = new HashMap<>();

    public BlankConfig(Object rawData) {
        if(!(rawData instanceof Map<?,?>))
            return;
        Map<?, ?> mapData = (Map<?, ?>) rawData;

        for(Object key : mapData.keySet())
            this.data.put(key.toString(), mapData.get(key));
    }

    public Object convertToObject() {
        return this.data;
    }

}
