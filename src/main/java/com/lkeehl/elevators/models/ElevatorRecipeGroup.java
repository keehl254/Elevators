package com.lkeehl.elevators.models;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.services.DataContainerService;
import com.lkeehl.elevators.services.configs.ConfigRecipe;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElevatorRecipeGroup {

    private final String recipeKey;

    private final ElevatorType elevatorType;

    private final ConfigRecipe recipeConfig;

    private final List<ElevatorRecipe> recipeList = new ArrayList<>();

    private final Map<Character, Material> materialMap = new HashMap<>();

    public ElevatorRecipeGroup(String recipeKey, ElevatorType elevatorType, ConfigRecipe recipeConfig) {
        this.recipeKey = recipeKey;
        this.elevatorType = elevatorType;
        this.recipeConfig = recipeConfig;

        this.loadMaterialMap();

        if(recipeConfig.coloredCrafting) {
            for (DyeColor color : DyeColor.values())
                this.addRecipe(this.recipeConfig.permission + "." + color.toString().toLowerCase(), color);
        }else
            this.addRecipe(this.recipeConfig.permission, this.elevatorType.getDefaultElevatorColor());

    }

    private void loadMaterialMap() {
        Map<String, String> stringMaterialMap = this.recipeConfig.materials;

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
        if(this.recipeConfig.coloredCrafting)
            return ItemStackHelper.getVariant(initialType, color);
        return initialType;
    }

    public boolean doesPermissibleHavePermissionForRecipe(Permissible permissible, ShapedRecipe recipe) {
        if(this.recipeConfig.coloredCrafting)
            return permissible.hasPermission(this.recipeConfig.permission);
        if(permissible.hasPermission(this.recipeConfig.permission + ".*"))
            return true;

        return this.recipeList.stream().filter(i ->recipe.getKey().equals(i.namespacedKey)).anyMatch(i -> permissible.hasPermission(i.permission));
    }

    public List<NamespacedKey> getNameSpacedKeys() {
        return this.recipeList.stream().map(i ->i.namespacedKey).collect(Collectors.toList());
    }

    private void addRecipe(String permission, DyeColor dyeColor) {

        NamespacedKey namespacedKey = DataContainerService.createKey(dyeColor.toString() + "_" + this.elevatorType.getTypeKey() + "_" + this.recipeKey + "_ELEVATOR");
        ItemStack elevatorItemStack = ItemStackHelper.createItemStackFromElevatorType(this.elevatorType, dyeColor);
        elevatorItemStack.setAmount(this.recipeConfig.amount);

        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, elevatorItemStack);
        recipe.shape(this.recipeConfig.recipe.toArray(new String[]{}));

        for(char character : this.materialMap.keySet())
            recipe.setIngredient(character, getMaterialVariant(this.materialMap.get(character), dyeColor));

        this.recipeList.add(new ElevatorRecipe(permission, namespacedKey, recipe));

        Bukkit.addRecipe(recipe);

    }

    private record ElevatorRecipe(String permission, NamespacedKey namespacedKey, ShapedRecipe recipe) {
    }



}
