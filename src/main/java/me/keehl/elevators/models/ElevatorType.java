package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.*;
import me.keehl.elevators.api.services.IElevatorActionService;
import me.keehl.elevators.api.services.IElevatorHologramService;
import me.keehl.elevators.api.services.IElevatorRecipeService;
import me.keehl.elevators.api.services.IElevatorSettingService;
import me.keehl.elevators.models.settings.ElevatorSetting;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigElevatorType;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigSettings;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.*;
import java.util.stream.Collectors;

public class ElevatorType extends ConfigElevatorType implements IElevatorType {

    //region properties
    private transient String elevatorTypeKey;

    private final transient List<IElevatorAction> actionsUp = new ArrayList<>();
    private final transient List<IElevatorAction> actionsDown = new ArrayList<>();

    //endregion

    /* region property getters */

    /**
     * @return the type name of the elevator.
     */
    public String getTypeKey() {
        return this.elevatorTypeKey.toUpperCase();
    }

    public List<IElevatorAction> getActionsUp() {
        return this.actionsUp;
    }

    public List<IElevatorAction> getActionsDown() {
        return this.actionsDown;
    }

    public List<IElevatorRecipeGroup> getRecipeGroups() {
        return new ArrayList<>(this.recipes.values());
    }

    public ConfigElevatorType getConfig() {
        return this;
    }

    public ConfigSettings getSettingsConfig() {
        return this.settings;
    }

    //endregion

    /* region property setters */

    /**
     * Sets the elevators ItemStack display name.
     */
    public void setDisplayName(ILocaleComponent displayName) {
        this.settings.displayName = displayName;

        IElevatorRecipeService recipeService = Bukkit.getServicesManager().load(IElevatorRecipeService.class);
        if (recipeService != null) {
            recipeService.refreshRecipes();
        }

        Elevators.getInstance().saveConfig();
    }

    /**
     * Sets the elevators use permission.
     */
    public void setUsePermission(String usePermission) {
        this.settings.usePermission = usePermission;
        Elevators.getInstance().saveConfig();
    }

    /**
     * Sets the elevators dye permission.
     */
    public void setDyePermission(String dyePermission) {
        this.settings.dyePermission = dyePermission;
        Elevators.getInstance().saveConfig();
    }


    /**
     * Sets the maximum distance that an elevator will search for a destination elevator.
     */
    public void setMaxDistanceAllowedBetweenElevators(int maxDistance) {
        this.settings.maxDistance = maxDistance;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Sets the max stack size of an elevator ItemStack
     */
    public void setMaxStackSize(int maxStackSize) {
        this.settings.maxStackSize = maxStackSize;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Sets the maximum amount of non-air blocks that can be between the origin and destination elevator before
     * it stops searching.
     */
    public void setMaxSolidBlocksAllowedBetweenElevators(int maxSolidBlocks) {
        this.settings.maxSolidBlocks = maxSolidBlocks;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Sets whether the elevator must teleport to destination elevator of the same type.
     */
    public void setCheckDestinationElevatorType(boolean checkType) {
        this.settings.classCheck = checkType;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether the elevator should check permissions to allow operation. This is mostly
     * used for those small, local servers where permissions plugins aren't used.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public void setElevatorRequiresPermissions(boolean checkPerms) {
        this.settings.checkPerms = checkPerms;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether the elevator can explode from creepers, tnt, etc.
     */
    public void setCanElevatorExplode(boolean canExplode) {
        this.settings.canExplode = canExplode;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether a recipe for this elevator type can produce a colored elevator.
     */
    public void setCanDye(boolean supportDying) {
        this.settings.supportDying = supportDying;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether an elevator can be teleported to if the destination will place the player inside
     * of a block.
     */
    public void setStopsObstructedTeleportation(boolean stopsObstruction) {
        this.settings.stopObstruction = stopsObstruction;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether an elevator can be teleported to if the destination is a separate color than the origin.
     */
    public void setShouldValidateColor(boolean checkColor) {
        this.settings.checkColor = checkColor;

        Elevators.getInstance().saveConfig();
    }

    /**
     * Set whether users can open the settings-edit menu of an elevator type.
     */
    public void setShouldAllowIndividualEdit(boolean allowIndividualEdit) {
        this.settings.canEditIndividually = allowIndividualEdit;
        Elevators.getInstance().saveConfig();
    }

    /**
     * Set the lines that should appear over an elevator of this type.
     */
    public void setHologramLines(List<ILocaleComponent> holoLines) {
        boolean checkCreate = this.settings.hologramLines.isEmpty();
        this.settings.hologramLines = holoLines;

        Elevators.getInstance().saveConfig();

        IElevatorHologramService hologramService = Bukkit.getServicesManager().load(IElevatorHologramService.class);
        if (hologramService == null || !hologramService.canUseHolograms())
            return;

        this.updateAllHolograms(checkCreate);
    }

    public void updateAllHolograms(boolean chunkCheck) {

        IElevatorHologramService hologramService = Bukkit.getServicesManager().load(IElevatorHologramService.class);
        if (hologramService == null)
            return;

        // Run this later so that we don't hold up admin or interact menus.
        Elevators.getFoliaLib().getScheduler().runNextTick(task -> hologramService.updateHologramsOfElevatorType(this));

        if (!chunkCheck)
            return;

        // Oof, this is a lot of nesting. Hate that, but don't want to extract to a method.
        Elevators.getFoliaLib().getScheduler().runNextTick(task -> {
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    hologramService.updateHologramsInChunk(chunk);
                }
            }
        });
    }

    /**
     * Set the lines that should appear in an itemstack of this type.
     */
    public void setLore(List<ILocaleComponent> loreLines) {
        this.settings.loreLines = loreLines;

        IElevatorRecipeService recipeService = Bukkit.getServicesManager().load(IElevatorRecipeService.class);
        if (recipeService != null) {
            recipeService.refreshRecipes();
        }

        Elevators.getInstance().saveConfig();
    }

    //endregion

    @Override()
    public void onSave() {
        this.actions.up = this.getActionsUp().stream().map(IElevatorAction::serialize).collect(Collectors.toList());
        this.actions.down = this.getActionsDown().stream().map(IElevatorAction::serialize).collect(Collectors.toList());

        IElevatorSettingService settingService = Bukkit.getServicesManager().load(IElevatorSettingService.class);
        if(settingService == null)
            return;

        for (IElevatorSetting<?> apiSetting : settingService.getElevatorSettings()) {
            if(!(apiSetting instanceof ElevatorSetting<?> setting))
                return;
            setting.applyToElevatorSettings(this, this.settings);
        }
    }

    @Override()
    public void onLoad() {
        this.getActionsUp().clear();
        this.getActionsDown().clear();

        IElevatorActionService actionService = Bukkit.getServicesManager().load(IElevatorActionService.class);
        if(actionService == null)
            return;

        this.getActionsUp().addAll(this.actions.up.stream().map(i -> actionService.createActionFromString(this, i)).filter(Objects::nonNull).toList());
        this.getActionsDown().addAll(this.actions.down.stream().map(i -> actionService.createActionFromString(this, i)).filter(Objects::nonNull).toList());

        // Enforce uppercase recipe keys.

        Map<String, IElevatorRecipeGroup> newRecipes = new HashMap<>();
        for (String key : this.recipes.keySet()) {
            IElevatorRecipeGroup recipeGroup = this.recipes.get(key);
            newRecipes.put(key.toUpperCase(), recipeGroup);
            recipeGroup.setKey(key.toUpperCase());
        }

        this.recipes = newRecipes;
    }

    @Override()
    public void setKey(String key) {
        this.elevatorTypeKey = key.toUpperCase();
    }


}
