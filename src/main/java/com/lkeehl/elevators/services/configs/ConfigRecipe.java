package com.lkeehl.elevators.services.configs;

import com.lkeehl.elevators.util.config.Comments;
import com.lkeehl.elevators.util.config.Config;
import org.bukkit.DyeColor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConfigRecipe implements Config {

    public int amount = 1;

    @Comments("If \"supportMultiColorMaterials\" is disabled, this permission will allow crafting as is. If \"supportMultiColorMaterials\" is enabled, a wildcard or dyecolor needs to be appended to this permission for proper checking.")
    protected String craftPermission = "elevators.craft.default";

    @Comments("This option controls the elevator output color if \"supportMultiColorOutput\" is disabled.")
    protected DyeColor defaultOutputColor = DyeColor.RED;

    @Comments("If this option is disabled, all variations of this recipe will be of the \"defaultOutputColor\".")
    protected boolean supportMultiColorOutput = true;

    @Comments("If this option is enabled, multiple variations of this recipe will be created for each dyed color.")
    protected boolean supportMultiColorMaterials = true;

    @Comments("Create a shaped recipe using unique characters here and map these characters to materials in the \"material\" config option.")
    protected List<String> recipe = Arrays.asList("www","wew","www");

    @Comments("Map characters to their materials. If \"supportMultiColorMaterials\" is enabled, dye colors in materials such as \"white\" in \"white_wool\" will be substituted for different dye colors.")
    protected Map<String, String> materials = Map.of("w","white_wool","e","ender_pearl");

}
