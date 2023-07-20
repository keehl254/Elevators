package com.lkeehl.elevators.services.configs;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Arrays;
import java.util.List;

@ConfigSerializable()
public class ConfigRecipe {

    public int amount = 1;

    public String permission = "elevators.craft.default";

    public boolean coloredCrafting = true;

    public List<String> recipe = Arrays.asList("www","wew","www");

}
