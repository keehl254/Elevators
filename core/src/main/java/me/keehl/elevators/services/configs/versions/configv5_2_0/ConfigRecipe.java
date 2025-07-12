package me.keehl.elevators.services.configs.versions.configv5_2_0;

import me.keehl.elevators.util.config.Comments;
import me.keehl.elevators.util.config.Config;
import me.keehl.elevators.util.config.RecipeRow;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.*;

public class ConfigRecipe implements Config {

    public int amount = 1;

    @Comments("If \"supportMultiColorMaterials\" is disabled, this permission will allow crafting as is. If \"supportMultiColorMaterials\" is enabled, a wildcard or dye-color needs to be appended to this permission for proper checking.")
    protected String craftPermission = "elevators.craft.default";

    @Comments("This option controls the elevator output color if \"supportMultiColorOutput\" is disabled.")
    protected DyeColor defaultOutputColor = DyeColor.RED;

    @Comments("If this option is disabled, all variations of this recipe will be of the \"defaultOutputColor\".")
    protected boolean supportMultiColorOutput = true;

    @Comments("If this option is enabled, multiple variations of this recipe will be created for each dyed color.")
    protected boolean supportMultiColorMaterials = true;

    @Comments("Create a shaped recipe using item keys. You may use elevators:elevator_key to require an elevator in your recipe in Minecraft 1.21+")
    protected List<RecipeRow<NamespacedKey>> recipe = Arrays.asList(
            new RecipeRow<>(Arrays.asList(Material.WHITE_WOOL.getKey(), Material.WHITE_WOOL.getKey(), Material.WHITE_WOOL.getKey())),
            new RecipeRow<>(Arrays.asList(Material.WHITE_WOOL.getKey(), Material.ENDER_PEARL.getKey(), Material.WHITE_WOOL.getKey())),
            new RecipeRow<>(Arrays.asList(Material.WHITE_WOOL.getKey(), Material.WHITE_WOOL.getKey(), Material.WHITE_WOOL.getKey()))
    );

    public static void setAmount(ConfigRecipe recipe, int amount) {
        recipe.amount = amount;
    }

    public static void setCraftPermission(ConfigRecipe recipe, String craftPermission) {
        recipe.craftPermission = craftPermission;
    }

    public static void setDefaultOutputColor(ConfigRecipe recipe, DyeColor defaultOutputColor) {
        recipe.defaultOutputColor = defaultOutputColor;
    }

    public static void setMultiColorOutput(ConfigRecipe recipe, boolean supportMultiColorOutput) {
        recipe.supportMultiColorOutput = supportMultiColorOutput;
    }

    public static void setMultiColorMaterials(ConfigRecipe recipe, boolean supportMultiColorMaterials) {
        recipe.supportMultiColorMaterials = supportMultiColorMaterials;
    }

    public static void setRecipe(ConfigRecipe recipe, List<RecipeRow<NamespacedKey>> recipeKeys) {
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
