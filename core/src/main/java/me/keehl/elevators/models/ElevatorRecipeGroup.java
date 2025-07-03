package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.services.ElevatorDataContainerService;
import me.keehl.elevators.services.ElevatorHookService;
import me.keehl.elevators.services.ElevatorTypeService;
import me.keehl.elevators.services.configs.versions.configv5_1_0.ConfigRecipe;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.permissions.Permissible;

import java.util.*;
import java.util.stream.Collectors;

public class ElevatorRecipeGroup extends ConfigRecipe {

    private transient String recipeKey;

    private transient ElevatorType elevatorType;

    private transient final List<ElevatorRecipe> recipeList = new ArrayList<>();

    @Override()
    public void setKey(String key) {
        this.recipeKey = key != null ? key.toUpperCase() : null;
    }

    public void load(ElevatorType elevatorType) {
        this.elevatorType = elevatorType;

        this.refreshRecipes();
    }

    public void refreshRecipes() {

        this.recipeList.clear();

        if (this.supportMultiColorMaterials) {
            for (DyeColor color : DyeColor.values())
                this.addRecipe(this.craftPermission + "." + color.toString().toLowerCase(), color);
        } else
            this.addRecipe(this.craftPermission, this.defaultOutputColor);

    }

    public String getRecipeKey() {
        return this.recipeKey;
    }

    public boolean supportMultiColorOutput() {
        return this.supportMultiColorOutput;
    }

    public boolean supportMultiColorMaterials() {
        return this.supportMultiColorMaterials;
    }

    /* Note to anyone working with this method: A new recipe is registered for each color, so the namespacedKey check
    on the last line is enough for checking colored crafting permission.
     */
    public <T extends Recipe & Keyed> boolean doesPermissibleHavePermissionForRecipe(Permissible permissible, T recipe) {
        if (!this.supportMultiColorMaterials)
            return permissible.hasPermission(this.craftPermission);
        if (permissible.hasPermission(this.craftPermission + ".*"))
            return true;

        return this.recipeList.stream().filter(i -> recipe.getKey().equals(i.getNamespacedKey())).anyMatch(i -> permissible.hasPermission(i.getPermission()));
    }

    public List<NamespacedKey> getNameSpacedKeys() {
        return this.recipeList.stream().map(ElevatorRecipe::getNamespacedKey).collect(Collectors.toList());
    }

    private void addRecipe(String permission, DyeColor dyeColor) {

        NamespacedKey namespacedKey = ElevatorDataContainerService.createKey(dyeColor.toString() + "_" + this.elevatorType.getTypeKey() + "_" + this.recipeKey + "_ELEVATOR");

        DyeColor elevatorColor = this.supportMultiColorOutput ? dyeColor : this.defaultOutputColor;

        ItemStack elevatorItemStack = ItemStackHelper.createItemStackFromElevatorType(this.elevatorType, elevatorColor);
        elevatorItemStack.setAmount(this.amount);

        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, elevatorItemStack);

        String[] shape = {"", "", ""};
        List<Runnable> setIngredientRunnables = new ArrayList<>();
        char currentChar = 'A';
        int rowIndex = 0;
        for (List<NamespacedKey> recipeRow : this.recipe) {
            for (NamespacedKey key : recipeRow) {
                ItemStack item = ElevatorHookService.createItemStackFromKey(key);
                if (item == null || item.getType().isAir()) {
                    shape[rowIndex] += " ";
                    continue;
                }
                char character = currentChar;
                shape[rowIndex] += character;

                // TODO: Apparently Spigot hates you adding the ingredient before the shape. What a pain.
                Runnable addIncredientRunnable;
                if (key.getNamespace().equalsIgnoreCase(NamespacedKey.MINECRAFT)) {
                    addIncredientRunnable = () -> shapedRecipe.setIngredient(character, ItemStackHelper.getVariant(item.getType(), dyeColor));
                } else if (key.getNamespace().equalsIgnoreCase(Elevators.getInstance().getName().toLowerCase(Locale.ROOT))) {
                    ElevatorType type = ElevatorTypeService.getElevatorType(key.getKey());
                    if (type != null)
                        type = ElevatorTypeService.getDefaultElevatorType();

                    final ElevatorType finalType = type;
                    addIncredientRunnable = () -> shapedRecipe.setIngredient(character, ItemStackHelper.createItemStackFromElevatorType(finalType, dyeColor));
                } else {
                    addIncredientRunnable = () -> shapedRecipe.setIngredient(character, item);
                }

                setIngredientRunnables.add(addIncredientRunnable);

                currentChar++;
            }
            rowIndex++;
        }

        shapedRecipe.shape(shape);
        setIngredientRunnables.forEach(Runnable::run);

        this.recipeList.add(new ElevatorRecipe(permission, namespacedKey, shapedRecipe));
        Bukkit.addRecipe(shapedRecipe);
    }

    private static class ElevatorRecipe {

        private final String permission;
        private final NamespacedKey namespacedKey;
        private final ShapedRecipe recipe;

        public ElevatorRecipe(String permission, NamespacedKey namespacedKey, ShapedRecipe recipe) {
            this.permission = permission;
            this.namespacedKey = namespacedKey;
            this.recipe = recipe;
        }

        public String getPermission() {
            return this.permission;
        }

        public NamespacedKey getNamespacedKey() {
            return this.namespacedKey;
        }

        public ShapedRecipe getRecipe() {
            return this.recipe;
        }
    }


}
