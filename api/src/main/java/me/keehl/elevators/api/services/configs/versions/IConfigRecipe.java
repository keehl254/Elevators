package me.keehl.elevators.api.services.configs.versions;

import me.keehl.elevators.api.util.config.Config;
import me.keehl.elevators.api.util.config.RecipeRow;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;

import java.util.List;

public interface IConfigRecipe extends Config {

    int getAmount();

    String getCraftPermission();

    DyeColor getDefaultOutputColor();

    boolean supportsMultiColorOutput();

    boolean supportsMultiColorMaterials();

    List<RecipeRow<NamespacedKey>> getRecipe();

}
