package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.IElevatorActionService;
import me.keehl.elevators.events.ElevatorRegisterActionsEvent;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.api.util.TriFunction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElevatorActionService extends ElevatorService implements IElevatorActionService {

    // This should probably be turned into a DTO.
    private final Map<String, TriFunction<JavaPlugin,IElevatorType, String, IElevatorAction>> actionConstructors = new HashMap<>();
    private final Map<String, ItemStack> actionIcons = new HashMap<>();
    private final Map<String, JavaPlugin> actionPlugins = new HashMap<>();
    protected Runnable registerDefaultActionsRunnable;

    private boolean initialized = false;
    private boolean allowSelfRegister = false;

    public ElevatorActionService(IElevators elevators) {
        super(elevators);
    }

    @Override
    public void onInitialize() {
        if(this.initialized)
            return;
        ElevatorsAPI.pushAndHoldLog();
        this.registerDefaultActions();
        this.initialized = true;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Action service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    @Override
    public void onUninitialize() {

    }

    private void registerDefaultActions() {

        this.allowSelfRegister = true;
        if(this.registerDefaultActionsRunnable != null) {
            this.registerDefaultActionsRunnable.run();
        }
        this.allowSelfRegister = false;

        Bukkit.getPluginManager().callEvent(new ElevatorRegisterActionsEvent());
    }

    @Override
    public void registerElevatorAction(JavaPlugin plugin, String key, TriFunction<JavaPlugin,IElevatorType, String, IElevatorAction> actionConstructor, ItemStack icon) {

        if(plugin.getName().equalsIgnoreCase(Elevators.getInstance().getName()) && !this.allowSelfRegister)
            throw new RuntimeException("An invalid Plugin was provided when trying to register an Elevator Action.");

        key = key.toLowerCase().trim();
        this.actionIcons.put(key, icon);
        this.actionPlugins.put(key, plugin);
        this.actionConstructors.put(key, actionConstructor);

        if(!Elevators.isInitialized())
            return;

        for(IElevatorType type : Elevators.getElevatorTypeService().getExistingElevatorTypes()) {
            type.onLoad();
        }
    }

    @Override
    public void registerElevatorAction(JavaPlugin plugin, String key, TriFunction<JavaPlugin,IElevatorType, String, IElevatorAction> actionConstructor, String chatColor, String displayName, Material itemType) {
        registerElevatorAction(plugin, key, actionConstructor, ItemStackHelper.createItem(chatColor + ChatColor.BOLD + displayName, itemType, 1));
    }

    @Override
    public IElevatorAction createActionFromString(IElevatorType elevatorType, String actionString) {
        if(!actionString.contains(":"))
            return null;
        String key = actionString.substring(0, actionString.indexOf(':')).toLowerCase();
        actionString = actionString.substring(actionString.indexOf(':') + 1);
        if (!this.actionConstructors.containsKey(key))
            return null;
        if (!this.actionPlugins.containsKey(key))
            return null;

        IElevatorAction action = this.actionConstructors.get(key).apply(this.actionPlugins.get(key), elevatorType, key);
        action.initialize(actionString);
        action.setIcon(this.actionIcons.get(key));

        return action;
    }

    @Override
    public List<String> getRegisteredActions() {
        return new ArrayList<>(this.actionIcons.keySet());
    }

    public ItemStack getActionIcon(String actionKey) {
        return this.actionIcons.getOrDefault(actionKey, null);
    }

    @Override
    public IElevatorAction createBlankAction(IElevatorType elevatorType, String actionKey) {
        actionKey = actionKey.toLowerCase().trim();
        if(!this.actionConstructors.containsKey(actionKey))
            return null;
        if (!this.actionPlugins.containsKey(actionKey))
            return null;

        IElevatorAction action = this.actionConstructors.get(actionKey).apply(this.actionPlugins.get(actionKey), elevatorType, actionKey);
        action.initialize("");
        action.setIcon(this.actionIcons.get(actionKey));

        return action;
    }



}
