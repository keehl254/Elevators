package me.keehl.elevators.services.configs.versions.configv4_0_2;

import me.keehl.elevators.util.config.Config;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V4_0_2ConfigRecipe implements Config {

    public int amount = 1;

    public String permission = "elevators.craft.classic";

    public boolean coloredCrafting = true;

    public List<String> recipe = Arrays.asList("www","wew","www");

    public Map<Character, Material> materials = new HashMap<Character, Material>() {{
        put('w', Material.WHITE_WOOL);
        put('e', Material.ENDER_PEARL);
    }};

}
