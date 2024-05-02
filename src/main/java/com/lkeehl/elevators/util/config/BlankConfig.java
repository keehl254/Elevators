package com.lkeehl.elevators.util.config;

import java.util.HashMap;
import java.util.Map;

public class BlankConfig implements Config {

    public transient Map<String, Object> data = new HashMap<>();

    public BlankConfig(Object rawData) {
        if(!(rawData instanceof Map<?,?> mapData))
            return;

        for(Object key : mapData.keySet())
            data.put(key.toString(), mapData.get(key));
    }

    public Object convertToObject() {
        return data;
    }

}
