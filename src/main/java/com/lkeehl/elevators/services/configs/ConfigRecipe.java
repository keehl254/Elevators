package com.lkeehl.elevators.services.configs;

import com.lkeehl.elevators.util.config.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConfigRecipe implements Config {

    public int amount = 1;

    public String craftPermission = "elevators.craft.default";

    public boolean coloredCrafting = true;

    public List<String> recipe = Arrays.asList("www","wew","www");

    public Map<String, String> materials;

}
