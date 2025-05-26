package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.*;
import me.keehl.elevators.events.ElevatorRegisterActionsEvent;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.ElevatorAction;
import me.keehl.elevators.models.ElevatorType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ElevatorActionService {

    private static final Map<String, BiFunction<ElevatorType, String, ElevatorAction>> actionConstructors = new HashMap<>();
    private static final Map<String, ItemStack> actionIcons = new HashMap<>();

    private static boolean initialized = false;

    public static void init() {
        if(ElevatorActionService.initialized)
            return;

        ElevatorActionService.registerDefaultActions();
        ElevatorActionService.initialized = true;
    }

    private static void registerDefaultActions() {

        registerElevatorAction("sound", SoundAction::new, ChatColor.GREEN.toString(), "Sound", Material.MUSIC_DISC_MALL);
        registerElevatorAction("command-console", CommandConsoleAction::new, ChatColor.DARK_RED.toString(), "Console Command", Material.COMMAND_BLOCK);
        registerElevatorAction("command-player", CommandPlayerAction::new, ChatColor.LIGHT_PURPLE.toString(), "Player Command", Material.REPEATING_COMMAND_BLOCK);
        registerElevatorAction("message-player", MessagePlayerAction::new, ChatColor.YELLOW.toString(), "Message User", Material.WRITTEN_BOOK);
        registerElevatorAction("message-all", MessageAllAction::new, ChatColor.RED.toString(), "Broadcast Message", Material.ENCHANTED_BOOK);

        registerElevatorAction("effect", EffectAction::new, ChatColor.BLUE.toString(), "Effect", Material.FIREWORK_ROCKET);

        registerElevatorAction("title", TitleAction::new, ChatColor.LIGHT_PURPLE.toString(), "Title", Material.NAME_TAG);
        registerElevatorAction("action-bar", ActionBarAction::new, ChatColor.YELLOW.toString(), "Action Bar", Material.BELL);
        registerElevatorAction("boss-bar", BossBarAction::new, ChatColor.RED.toString(), "Boss Bar", Material.DRAGON_HEAD);

        registerElevatorAction("charge-exp", ChargeExpAction::new, ChatColor.GOLD.toString(), "Charge EXP", Material.EXPERIENCE_BOTTLE);

        Bukkit.getPluginManager().callEvent(new ElevatorRegisterActionsEvent());
    }

    public static void registerElevatorAction(String key, BiFunction<ElevatorType, String, ElevatorAction> actionConstructor, ItemStack icon) {

        key = key.toLowerCase().trim();
        actionIcons.put(key, icon);
        actionConstructors.put(key, actionConstructor);

        if(!Elevators.isInitialized())
            return;

        for(ElevatorType type : ElevatorTypeService.getExistingElevatorTypes()) {
            type.onLoad();
        }
    }

    public static void registerElevatorAction(String key, BiFunction<ElevatorType, String, ElevatorAction> actionConstructor, String chatColor,  String displayName, Material itemType) {
        registerElevatorAction(key, actionConstructor, ItemStackHelper.createItem(chatColor + ChatColor.BOLD + displayName, itemType, 1));
    }

    public static ElevatorAction createActionFromString(ElevatorType elevatorType, String actionString) {
        if(!actionString.contains(":"))
            return null;
        String key = actionString.substring(0, actionString.indexOf(':')).toLowerCase();
        actionString = actionString.substring(actionString.indexOf(':') + 1);
        if (!actionConstructors.containsKey(key))
            return null;

        ElevatorAction action = actionConstructors.get(key).apply(elevatorType, key);
        action.initialize(actionString);
        action.setIcon(actionIcons.get(key));

        return action;
    }

    public static List<String> getRegisteredActions() {
        return new ArrayList<>(actionIcons.keySet());
    }

    public static ItemStack getActionIcon(String actionKey) {
        return actionIcons.getOrDefault(actionKey, null);
    }

    public static ElevatorAction createBlankAction(ElevatorType elevatorType, String actionKey) {
        actionKey = actionKey.toLowerCase().trim();
        Elevators.getElevatorsLogger().info(actionKey);
        if(!actionConstructors.containsKey(actionKey))
            return null;

        ElevatorAction action = actionConstructors.get(actionKey).apply(elevatorType, actionKey);
        action.initialize("");
        action.setIcon(actionIcons.get(actionKey));

        return action;
    }



}
