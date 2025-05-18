package com.lkeehl.elevators.services.configs.versions.configv4_0_2;

import com.lkeehl.elevators.util.config.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class V4_0_2ConfigRecipe implements Config {

    public int amount = 1;

    public String permission = "elevators.craft.classic";

    public boolean coloredCrafting = true;

    public List<String> recipe = Arrays.asList("www","wew","www");

    public Map<String, String> materials = Map.of("w","white_wool","e","ender_pearl");

}
