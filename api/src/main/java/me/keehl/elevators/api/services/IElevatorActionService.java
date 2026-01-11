package me.keehl.elevators.api.services;

import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.TriFunction;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public interface IElevatorActionService extends IElevatorService {

    void registerElevatorAction(JavaPlugin plugin, String key, TriFunction<JavaPlugin, IElevatorType, String, IElevatorAction> actionConstructor, ItemStack icon);

    void registerElevatorAction(JavaPlugin plugin, String key, TriFunction<JavaPlugin,IElevatorType, String, IElevatorAction> actionConstructor, String chatColor, String displayName, Material itemType);

    IElevatorAction createActionFromString(IElevatorType elevatorType, String actionString);

    List<String> getRegisteredActions();

    IElevatorAction createBlankAction(IElevatorType elevatorType, String actionKey);



}
