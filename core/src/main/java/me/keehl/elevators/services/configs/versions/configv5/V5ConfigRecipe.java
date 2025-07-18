package me.keehl.elevators.services.configs.versions.configv5;

import me.keehl.elevators.util.config.Comments;
import me.keehl.elevators.util.config.Config;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.*;

public class V5ConfigRecipe implements Config {

    public int amount = 1;

    @Comments("If \"supportMultiColorMaterials\" is disabled, this permission will allow crafting as is. If \"supportMultiColorMaterials\" is enabled, a wildcard or dye-color needs to be appended to this permission for proper checking.")
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
    protected Map<Character, Material> materials = new HashMap<Character, Material>() {{
        put('w',Material.WHITE_WOOL);
        put('e',Material.ENDER_PEARL);
    }};

    public static void setAmount(V5ConfigRecipe recipe, int amount) {
        recipe.amount = amount;
    }

    public static void setCraftPermission(V5ConfigRecipe recipe, String craftPermission) {
        recipe.craftPermission = craftPermission;
    }

    public static void setDefaultOutputColor(V5ConfigRecipe recipe, DyeColor defaultOutputColor) {
        recipe.defaultOutputColor = defaultOutputColor;
    }

    public static void setMultiColorOutput(V5ConfigRecipe recipe, boolean supportMultiColorOutput) {
        recipe.supportMultiColorOutput = supportMultiColorOutput;
    }

    public static void setMultiColorMaterials(V5ConfigRecipe recipe, boolean supportMultiColorMaterials) {
        recipe.supportMultiColorMaterials = supportMultiColorMaterials;
    }

    public static void setRecipe(V5ConfigRecipe recipe, List<String> recipeText) {
        recipe.recipe = new ArrayList<>(recipeText);
    }

    public static void setMaterials(V5ConfigRecipe recipe, Map<Character, Material> materialMap) {
        recipe.materials = new HashMap<>();
        recipe.materials.putAll(materialMap);
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

    public List<String> getRecipe() {
        return this.recipe;
    }

    public Map<Character, Material> getMaterials() {
        return this.materials;
    }

}
