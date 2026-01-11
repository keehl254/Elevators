package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.hooks.*;
import me.keehl.elevators.api.services.IElevatorHookService;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.api.models.IElevator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ElevatorHookService extends ElevatorService implements IElevatorHookService {

    private boolean initialized = false;

    private final Map<String, ElevatorHook> hookMap = new HashMap<>();

    private PlaceholderHook placeholderHook = null;
    private HologramHook hologramHook = null;

    public ElevatorHookService(IElevators elevators) {
        super(elevators);
    }

    public void onInitialize() {
        if(this.initialized)
            return;
        ElevatorsAPI.pushAndHoldLog();

        this.initialized = true;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Hook service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public void onUninitialize() {
        this.hookMap.clear();

        this.initialized = false;
    }

    public void registerHook(String pluginName, Class<? extends ElevatorHook> elevatorHookClass, boolean requireActive) {
       if(this.hookMap.containsKey(pluginName.toUpperCase()))
           return;

        if(Bukkit.getPluginManager().getPlugin(pluginName) == null)
            return;
       if(requireActive && !Bukkit.getPluginManager().isPluginEnabled(pluginName))
           return;

        try {
            ElevatorsAPI.pushAndHoldLog();
            Constructor<?> hookConstructor = elevatorHookClass.getConstructor();
            ElevatorHook hook = (ElevatorHook) hookConstructor.newInstance();
            this.hookMap.put(pluginName.toUpperCase(), hook);

            this.placeholderHook = this.hookMap.values().stream().filter(i -> i instanceof PlaceholderHook).map(i -> (PlaceholderHook) i).findFirst().orElse(null);
            this.hologramHook = this.hookMap.values().stream().filter(i -> i instanceof HologramHook).map(i -> (HologramHook) i).findFirst().orElse(null);

            hook.onInit();

            if(hook instanceof IProtectionHook protectionHook) {
                Elevators.getConfigService().addConfigCallback(root -> protectionHook.getConfig());
            }

            ElevatorsAPI.popLog((logData) -> ElevatorsAPI.log("Hooked into " + pluginName + ". "+ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));

        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            ElevatorsAPI.log(Level.WARNING, "Failed to register hook for \"" + pluginName + "\" due to an inaccessible constructor. The plugin will still function; however, this hook will not work. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues", e);
        }
    }

    public void registerHook(String pluginName, Class<? extends ElevatorHook> elevatorHookClass) {
        registerHook(pluginName, elevatorHookClass, true);
    }

    public boolean canUseElevator(Player player, IElevator elevator, boolean sendMessage) {
        try {
            return this.getProtectionHooks().stream().allMatch(hook -> !hook.isCheckEnabled(elevator) || hook.canPlayerUseElevator(player, elevator, sendMessage));
        } catch (Exception e) {
            ElevatorsAPI.log(Level.SEVERE, "Failed to check hooks for use permission. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return false;
    }

    public boolean canEditElevator(Player player, IElevator elevator, boolean sendMessage) {
        try {
            return this.getProtectionHooks().stream().allMatch(hook -> hook.canEditSettings(player, elevator, sendMessage));
        } catch (Exception e) {
            ElevatorsAPI.log(Level.SEVERE, "Failed to check hooks for edit permission. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return false;
    }

    public boolean canRenameElevator(Player player, IElevator elevator, boolean sendMessage) {
        try {
            return this.getProtectionHooks().stream().allMatch(hook -> hook.canEditName(player, elevator, sendMessage));
        } catch (Exception e) {
            ElevatorsAPI.log(Level.SEVERE, "Failed to check hooks for rename permission. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return false;
    }

    public ItemStack createItemStackFromKey(NamespacedKey key) {

        if(key.getNamespace().equalsIgnoreCase(NamespacedKey.MINECRAFT)) {
            Material type = Material.matchMaterial(key.getKey());
            if(type == null)
                return null;
            return new ItemStack(type,1);
        }

        if(key.getNamespace().equalsIgnoreCase(Elevators.getInstance().getName().toLowerCase(Locale.ROOT))) {
            IElevatorType type = Elevators.getElevatorTypeService().getElevatorType(key.getKey());
            if(type != null)
                return ItemStackHelper.createItemStackFromElevatorType(type, DyeColor.RED);
            return null;
        }

        try {
            for(ItemsHook hook : this.getItemsHooks()) {
                ItemStack item = hook.createItemStackFromKey(key);
                if(item != null)
                    return item;
            }
        } catch (Exception e) {
            ElevatorsAPI.log(Level.SEVERE, "Failed to get item from namespaced key. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return null;
    }

    public NamespacedKey getKeyFromItemStack(ItemStack item) {
        try {

            IElevatorType elevatorType = ElevatorHelper.getElevatorType(item);
            if(elevatorType != null)
                return new NamespacedKey(Elevators.getInstance(), elevatorType.getTypeKey().toLowerCase());

            for(ItemsHook hook : this.getItemsHooks()) {
                NamespacedKey key = hook.getKeyFromItemStack(item);
                if(key != null)
                    return key;
            }
        } catch (Exception e) {
            ElevatorsAPI.log(Level.SEVERE, "Failed to get namespaced key from itemstack. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        return item.getType().getKey();
    }

    public PlaceholderHook getPlaceholderHook() {
        return this.placeholderHook;
    }

    // Protected because we want all hologram alterations to be done through HologramService
    protected HologramHook getHologramHook() {
        return this.hologramHook;
    }

    public List<IProtectionHook> getProtectionHooks() {
        return this.hookMap.values().stream().filter(i -> i instanceof ProtectionHook).map(i -> (ProtectionHook) i).collect(Collectors.toList());
    }

    public List<ItemsHook> getItemsHooks() {
        return this.hookMap.values().stream().filter(i -> i instanceof ItemsHook).map(i -> (ItemsHook) i).collect(Collectors.toList());
    }

}
