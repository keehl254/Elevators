package me.keehl.elevators.api.models.actions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IElevatorActionBuilder {


    IElevatorActionBuilder onExecute(Consumer<IElevatorActionExecuteContext> executeConsumer);

    IElevatorActionBuilder onCheckConditions(Function<IElevatorActionExecuteContext, Boolean> conditionsConsumer);

    IElevatorActionBuilder onInit(Runnable onInit);

    <T> IElevatorActionBuilder addVariable(T defaultValue, Consumer<IElevatorActionVariableBuilder<T>> variableEditor);

    void register(JavaPlugin plugin, ItemStack icon);

    void register(JavaPlugin plugin, String chatColor,  String displayName, Material itemType);

}
