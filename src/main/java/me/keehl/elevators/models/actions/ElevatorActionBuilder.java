package me.keehl.elevators.models.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.IElevatorAction;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorActionVariable;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.actions.IElevatorActionBuilder;
import me.keehl.elevators.api.models.actions.IElevatorActionExecuteContext;
import me.keehl.elevators.api.models.actions.IElevatorActionVariableBuilder;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.api.util.TriFunction;
import me.keehl.elevators.util.exceptions.ElevatorActionBuilderException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

public class ElevatorActionBuilder implements IElevatorActionBuilder {

    private final List<ElevatorActionVariableBuilder<?>> groupings = new ArrayList<>();

    private final String actionKey;
    private Consumer<IElevatorActionExecuteContext> executeConsumer;
    private Function<IElevatorActionExecuteContext, Boolean> conditionsFunction = data -> true;
    private Runnable onInit = () -> {};

    public ElevatorActionBuilder(String actionKey) {
        this.actionKey = actionKey;
    }

    public ElevatorActionBuilder onExecute(Consumer<IElevatorActionExecuteContext> executeConsumer) {
        this.executeConsumer = executeConsumer;
        return this;
    }

    public ElevatorActionBuilder onCheckConditions(Function<IElevatorActionExecuteContext, Boolean> conditionsConsumer) {
        this.conditionsFunction = conditionsConsumer;
        return this;
    }

    public ElevatorActionBuilder onInit(Runnable onInit) {
        this.onInit = onInit;
        return this;
    }

    public <T> ElevatorActionBuilder addVariable(T defaultValue, Consumer<IElevatorActionVariableBuilder<T>> variableEditor) {
        ElevatorActionVariableBuilder<T> builder = new ElevatorActionVariableBuilder<>(defaultValue);
        variableEditor.accept(builder);

        try {
            builder.validate();
        } catch (Exception e) {
            throw new RuntimeException("Custom Elevator Action failed to add variable. Action: " + this.actionKey, e);
        }

        this.groupings.add(builder);
        return this;
    }

    public void register(JavaPlugin plugin, ItemStack icon) {
        TriFunction<JavaPlugin, IElevatorType, String, IElevatorAction> buildAction = (javaPlugin, type, actionKey) -> {

            Map<ElevatorActionVariableBuilder<?>, IElevatorActionVariable<?>> varBuilderXGroups = new HashMap<>();
            for(ElevatorActionVariableBuilder<?> builder : this.groupings)
                varBuilderXGroups.put(builder, builder.build());

            return new BuilderElevatorAction(plugin, this, actionKey, type, varBuilderXGroups);
        };

        Elevators.getActionService().registerElevatorAction(plugin, this.actionKey, buildAction, icon);
    }

    public void register(JavaPlugin plugin, String chatColor,  String displayName, Material itemType) {
        this.register(plugin, ItemStackHelper.createItem(chatColor + ChatColor.BOLD + displayName, itemType, 1));
    }

    public static class BuilderElevatorAction extends ElevatorAction {


        private final ElevatorActionBuilder builder;
        private final Map<ElevatorActionVariableBuilder<?>, IElevatorActionVariable<?>> variableBuilders;

        protected BuilderElevatorAction(JavaPlugin plugin, ElevatorActionBuilder builder, String actionKey, IElevatorType elevatorType, Map<ElevatorActionVariableBuilder<?>, IElevatorActionVariable<?>> variableBuilders) {
            super(plugin, elevatorType, actionKey, variableBuilders.values().toArray(new IElevatorActionVariable<?>[]{}));

            this.builder = builder;
            this.variableBuilders = variableBuilders;
        }

        @Override
        protected void onInitialize(String value) {
            try {
                for (ElevatorActionVariableBuilder<?> variableBuilder : this.variableBuilders.keySet())
                    variableBuilder.setup(this);
            }catch (ElevatorActionBuilderException e) {
                ElevatorsAPI.log(Level.WARNING, "Failed to create ElevatorAction. Issue:\n" + ResourceHelper.cleanTrace(e));
            }
            this.builder.onInit.run();
        }

        @Override
        public void execute(IElevatorEventData eventData, Player player) {
            this.builder.executeConsumer.accept(new ElevatorActionExecuteContext(this, eventData, player));
        }

        @Override
        public boolean meetsConditions(IElevatorEventData eventData, Player player) {
            return this.builder.conditionsFunction.apply(new ElevatorActionExecuteContext(this, eventData, player));
        }
    }



}
