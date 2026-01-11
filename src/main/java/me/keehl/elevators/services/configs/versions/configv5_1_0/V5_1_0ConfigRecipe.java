package me.keehl.elevators.services.configs.versions.configv5_1_0;

import me.keehl.elevators.api.util.config.Config;
import me.keehl.elevators.api.util.config.RecipeRow;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.Arrays;
import java.util.List;

public class V5_1_0ConfigRecipe implements Config {

    public int amount = 1;

    protected String craftPermission = "elevators.craft.default";
    protected DyeColor defaultOutputColor = DyeColor.RED;
    protected boolean supportMultiColorOutput = true;
    protected boolean supportMultiColorMaterials = true;
    protected List<RecipeRow<NamespacedKey>> recipe = Arrays.asList(
            new RecipeRow<>(Arrays.asList(Material.WHITE_WOOL.getKey(), Material.WHITE_WOOL.getKey(), Material.WHITE_WOOL.getKey())),
            new RecipeRow<>(Arrays.asList(Material.WHITE_WOOL.getKey(), Material.ENDER_PEARL.getKey(), Material.WHITE_WOOL.getKey())),
            new RecipeRow<>(Arrays.asList(Material.WHITE_WOOL.getKey(), Material.WHITE_WOOL.getKey(), Material.WHITE_WOOL.getKey()))
    );

    public static void setAmount(V5_1_0ConfigRecipe recipe, int amount) {
        recipe.amount = amount;
    }

    public static void setCraftPermission(V5_1_0ConfigRecipe recipe, String craftPermission) {
        recipe.craftPermission = craftPermission;
    }

    public static void setDefaultOutputColor(V5_1_0ConfigRecipe recipe, DyeColor defaultOutputColor) {
        recipe.defaultOutputColor = defaultOutputColor;
    }

    public static void setMultiColorOutput(V5_1_0ConfigRecipe recipe, boolean supportMultiColorOutput) {
        recipe.supportMultiColorOutput = supportMultiColorOutput;
    }

    public static void setMultiColorMaterials(V5_1_0ConfigRecipe recipe, boolean supportMultiColorMaterials) {
        recipe.supportMultiColorMaterials = supportMultiColorMaterials;
    }

    public static void setRecipe(V5_1_0ConfigRecipe recipe, List<RecipeRow<NamespacedKey>> recipeKeys) {
        recipe.recipe = recipeKeys;
    }

    public int getAmount() {
        return this.amount;
    }

    public String getCraftPermission() {
        return this.craftPermission;
    }

    public DyeColor getDefaultOutputColor() {
        return this.defaultOutputColor;
    }

    public boolean supportsMultiColorOutput() {
        return this.supportMultiColorOutput;
    }

    public boolean supportsMultiColorMaterials() {
        return this.supportMultiColorMaterials;
    }

    public List<RecipeRow<NamespacedKey>> getRecipe() {
        return this.recipe;
    }

}
