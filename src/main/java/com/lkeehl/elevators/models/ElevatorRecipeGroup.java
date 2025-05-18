package com.lkeehl.elevators.models;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.services.ElevatorDataContainerService;
import com.lkeehl.elevators.services.configs.versions.configv5.ConfigRecipe;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElevatorRecipeGroup extends ConfigRecipe {

    private transient String recipeKey;

    private transient ElevatorType elevatorType;

    private transient final List<ElevatorRecipe> recipeList = new ArrayList<>();

    private transient final Map<Character, Material> materialMap = new HashMap<>();

    @Override()
    public void setKey(String key) {
        this.recipeKey = key;
    }

    public void load(ElevatorType elevatorType) {
        this.elevatorType = elevatorType;

        this.loadMaterialMap();
        if(this.supportMultiColorMaterials) {
            for (DyeColor color : DyeColor.values())
                this.addRecipe(this.craftPermission + "." + color.toString().toLowerCase(), color);
        } else
            this.addRecipe(this.craftPermission, this.defaultOutputColor);
    }

    private void loadMaterialMap() {
        Map<String, String> stringMaterialMap = this.materials;

        for (String character : stringMaterialMap.keySet()) {
            String materialString = stringMaterialMap.get(character);

            Material type = Material.matchMaterial(materialString);
            if (type == null) {
                Elevators.getElevatorsLogger().warning("Elevators: There was an error loading \"" + this.recipeKey + "\" recipe of elevator type \"" + this.elevatorType.getTypeKey() + "\"! Reason: Invalid material \"" + materialString + "\"");
                return;
            }
            this.materialMap.put(character.charAt(0), type);
        }
    }

    private Material getMaterialVariant(Material initialType, DyeColor color) {
        return ItemStackHelper.getVariant(initialType, color);
    }

    /* Note to anyone working with this method: A new recipe is registered for each color, so the namespacedKey check
    on the last line is sufficient for checking colored crafting permission.
     */
    public <T extends Recipe & Keyed> boolean doesPermissibleHavePermissionForRecipe(Permissible permissible, T recipe) {
        if (!this.supportMultiColorMaterials)
            return permissible.hasPermission(this.craftPermission);
        if (permissible.hasPermission(this.craftPermission + ".*"))
            return true;

        return this.recipeList.stream().filter(i -> recipe.getKey().equals(i.namespacedKey)).anyMatch(i -> permissible.hasPermission(i.permission));
    }

    public List<NamespacedKey> getNameSpacedKeys() {
        return this.recipeList.stream().map(i -> i.namespacedKey).collect(Collectors.toList());
    }

    public DyeColor getDefaultOutputColor() {
        return this.defaultOutputColor;
    }

    private void addRecipe(String permission, DyeColor dyeColor) {

        NamespacedKey namespacedKey = ElevatorDataContainerService.createKey(dyeColor.toString() + "_" + this.elevatorType.getTypeKey() + "_" + this.recipeKey + "_ELEVATOR");

        DyeColor elevatorColor = this.supportMultiColorOutput ? dyeColor : this.defaultOutputColor;

        ItemStack elevatorItemStack = ItemStackHelper.createItemStackFromElevatorType(this.elevatorType, elevatorColor);
        elevatorItemStack.setAmount(this.amount);

        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, elevatorItemStack);
        recipe.shape(this.recipe.toArray(new String[]{}));

        for (char character : this.materialMap.keySet())
            recipe.setIngredient(character, getMaterialVariant(this.materialMap.get(character), dyeColor));

        this.recipeList.add(new ElevatorRecipe(permission, namespacedKey, recipe));
        Bukkit.addRecipe(recipe);
    }

    private record ElevatorRecipe(String permission, NamespacedKey namespacedKey, ShapedRecipe recipe) {
    }


}
