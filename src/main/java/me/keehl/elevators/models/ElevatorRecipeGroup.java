package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.ElevatorRecipe;
import me.keehl.elevators.api.models.IElevatorRecipeGroup;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.config.nodes.ConfigNode;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigRecipe;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class ElevatorRecipeGroup extends ConfigRecipe implements IElevatorRecipeGroup {

    private transient String recipeKey;

    private transient IElevatorType elevatorType;

    private transient final List<ElevatorRecipe> recipeList = new ArrayList<>();

    private final transient List<NamespacedKey> namespacedKeys = new ArrayList<>();

    @Override()
    public void setKey(String key) {
        this.recipeKey = key != null ? key.toUpperCase() : null;
    }

    @Override
    public void setNode(ConfigNode<?> node) {
        if(!(node.getParent().getParent().getValue() instanceof IElevatorType parentElevatorType))
            return;
        this.elevatorType = parentElevatorType;
    }

    public void load(IElevatorType elevatorType) {
        this.elevatorType = elevatorType;

        //this.refreshRecipes();
    }

    public String getRecipeKey() {
        return this.recipeKey;
    }

    public boolean supportsMultiColorOutput() {
        return this.supportMultiColorOutput;
    }

    public boolean supportsMultiColorMaterials() {
        return this.supportMultiColorMaterials;
    }

    public List<NamespacedKey> getNameSpacedKeys() {
        return this.namespacedKeys;
    }

    @Override
    public void createElevatorRecipes(Map<NamespacedKey, ElevatorRecipe> newRecipes) {
        this.namespacedKeys.clear();
        if (this.supportMultiColorMaterials) {
            for (DyeColor color : DyeColor.values()) {
                this.createElevatorRecipes(this.craftPermission + "." + color.toString().toLowerCase(), color, newRecipes);
            }
        } else {
            this.createElevatorRecipes(this.craftPermission, this.defaultOutputColor, newRecipes);
        }
    }

    private void createElevatorRecipes(String permission, DyeColor dyeColor, Map<NamespacedKey, ElevatorRecipe> recipes) {

        NamespacedKey namespacedKey = Elevators.getDataContainerService().createKey(dyeColor.toString() + "_" + this.elevatorType.getTypeKey() + "_" + this.recipeKey + "_ELEVATOR");

        DyeColor elevatorColor = this.supportMultiColorOutput ? dyeColor : this.defaultOutputColor;

        ItemStack elevatorItemStack = ItemStackHelper.createItemStackFromElevatorType(this.elevatorType, elevatorColor);
        elevatorItemStack.setAmount(this.amount);

        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, elevatorItemStack);
        shapedRecipe.setGroup(this.getRecipeKey());

        String[] shape = {"", "", ""};
        List<Runnable> setIngredientRunnables = new ArrayList<>();
        char currentChar = 'A';
        int rowIndex = 0;
        for (List<NamespacedKey> recipeRow : this.recipe) {
            for (NamespacedKey key : recipeRow) {
                ItemStack item = Elevators.getHooksService().createItemStackFromKey(key);
                if (item == null || item.getType().isAir()) {
                    shape[rowIndex] += " ";
                    continue;
                }
                char character = currentChar;
                shape[rowIndex] += character;

                Runnable addIncredientRunnable;
                if (key.getNamespace().equalsIgnoreCase(NamespacedKey.MINECRAFT)) {
                    addIncredientRunnable = () -> shapedRecipe.setIngredient(character, ItemStackHelper.getVariant(item.getType(), dyeColor));
                } else if (key.getNamespace().equalsIgnoreCase(Elevators.getInstance().getName().toLowerCase(Locale.ROOT))) {
                    IElevatorType type = Elevators.getElevatorTypeService().getElevatorType(key.getKey());
                    if (type == null) {
                        type = Elevators.getElevatorTypeService().getDefaultElevatorType();
                    }

                    final IElevatorType finalType = type;
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

        recipes.put(namespacedKey, new ElevatorRecipe(this, permission, namespacedKey, shapedRecipe));
        this.namespacedKeys.add(namespacedKey);
    }


}
