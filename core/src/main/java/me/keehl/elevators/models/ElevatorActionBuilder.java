package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.services.ElevatorActionService;
import me.keehl.elevators.util.TriFunction;
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

public class ElevatorActionBuilder {

    private final List<ElevatorActionVariableBuilder<?>> groupings = new ArrayList<>();

    private final String actionKey;
    private Consumer<ElevatorActionExecuteContext> executeConsumer;
    private Function<ElevatorActionExecuteContext, Boolean> conditionsFunction = data -> true;
    private Runnable onInit = () -> {};

    public ElevatorActionBuilder(String actionKey) {
        this.actionKey = actionKey;
    }

    public ElevatorActionBuilder onExecute(Consumer<ElevatorActionExecuteContext> executeConsumer) {
        this.executeConsumer = executeConsumer;
        return this;
    }

    public ElevatorActionBuilder onCheckConditions(Function<ElevatorActionExecuteContext, Boolean> conditionsConsumer) {
        this.conditionsFunction = conditionsConsumer;
        return this;
    }

    public ElevatorActionBuilder onInit(Runnable onInit) {
        this.onInit = onInit;
        return this;
    }

    public <T> ElevatorActionBuilder addVariable(T defaultValue, Consumer<ElevatorActionVariableBuilder<T>> variableEditor) {
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
        TriFunction<JavaPlugin, ElevatorType, String, ElevatorAction> buildAction = (javaPlugin, type, actionKey) -> {

            Map<ElevatorActionVariableBuilder<?>, ElevatorActionVariable<?>> varBuilderXGroups = new HashMap<>();
            for(ElevatorActionVariableBuilder<?> builder : this.groupings)
                varBuilderXGroups.put(builder, builder.build());

            return new BuilderElevatorAction(plugin, this, actionKey, type, varBuilderXGroups);
        };

        ElevatorActionService.registerElevatorAction(plugin, this.actionKey, buildAction, icon);
    }

    public void register(JavaPlugin plugin, String chatColor,  String displayName, Material itemType) {
        this.register(plugin, ItemStackHelper.createItem(chatColor + ChatColor.BOLD + displayName, itemType, 1));
    }

    public static class BuilderElevatorAction extends ElevatorAction {


        private final ElevatorActionBuilder builder;
        private final Map<ElevatorActionVariableBuilder<?>, ElevatorActionVariable<?>> variableBuilders;

        protected BuilderElevatorAction(JavaPlugin plugin, ElevatorActionBuilder builder, String actionKey, ElevatorType elevatorType, Map<ElevatorActionVariableBuilder<?>, ElevatorActionVariable<?>> variableBuilders) {
            super(plugin, elevatorType, actionKey, variableBuilders.values().toArray(new ElevatorActionVariable<?>[]{}));

            this.builder = builder;
            this.variableBuilders = variableBuilders;
        }

        @Override
        protected void onInitialize(String value) {
            try {
                for (ElevatorActionVariableBuilder<?> variableBuilder : this.variableBuilders.keySet())
                    variableBuilder.setup(this);
            }catch (ElevatorActionBuilderException e) {
                Elevators.log(Level.SEVERE, "Failed to create ElevatorAction Issue:\n" + ResourceHelper.cleanTrace(e));
            }
            this.builder.onInit.run();
        }

        @Override
        public void execute(ElevatorEventData eventData, Player player) {
            this.builder.executeConsumer.accept(new ElevatorActionExecuteContext(this, eventData, player));
        }

        @Override
        public boolean meetsConditions(ElevatorEventData eventData, Player player) {
            return this.builder.conditionsFunction.apply(new ElevatorActionExecuteContext(this, eventData, player));
        }
    }

    public static class ElevatorActionExecuteContext {

        private final ElevatorAction action;
        private final ElevatorEventData eventData;
        private final Player player;

        protected ElevatorActionExecuteContext(ElevatorAction action, ElevatorEventData eventData, Player player) {
            this.action = action;
            this.eventData = eventData;
            this.player = player;
        }

        @SuppressWarnings("unchecked")
        public <T> T getVariable(String alias) {
            Optional<ElevatorActionVariable<?>> groupingOptional = this.action.getGroupingByAlias(alias);
            if(!groupingOptional.isPresent())
                throw new RuntimeException("Attempt to pull Elevator Action Variable with alias that was not setup: " + this.action.getKey() + " -> " + alias);
            return (T) this.action.getVariableValue(groupingOptional.get(), this.eventData.getOrigin());
        }

        public ElevatorAction getAction() {
            return this.action;
        }

        public ElevatorEventData getEventData() {
            return this.eventData;
        }

        public Player getPlayer() {
            return this.player;
        }

    }

    public static class ElevatorActionVariableBuilder<T> {

        protected T defaultValue;
        protected Function<String, T> conversionFunction;
        protected String[] alias;
        protected String settingName;

        protected String description = "";
        protected String displayName;
        protected Material iconType;

        protected boolean allowPerEleCustomization = false;

        protected Consumer<ElevatorSettingClickContext<T>> onClick = ElevatorSettingClickContext::close;

        protected Map<String, String> actions = new HashMap<>();

        private ElevatorActionVariable<T> builtGrouping;

        public ElevatorActionVariableBuilder(T defaultValue) {
            this.defaultValue = defaultValue;
        }

        public ElevatorActionVariableBuilder<T> setDefault(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public ElevatorActionVariableBuilder<T> setConversion(Function<String, T> conversionFunction) {
            this.conversionFunction = conversionFunction;
            return this;
        }

        public ElevatorActionVariableBuilder<T> setAlias(String... alias) {
            this.alias = alias;
            if(this.settingName == null && alias.length > 0)
                this.settingName = alias[0];
            return this;
        }

        public ElevatorActionVariableBuilder<T> setIconDescription(String description) {
            this.description = description;
            return this;
        }

        public ElevatorActionVariableBuilder<T> setSettingName(String settingName) {
            this.settingName = settingName;
            if(this.displayName == null)
                this.displayName = settingName;
            return this;
        }

        public ElevatorActionVariableBuilder<T> setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public ElevatorActionVariableBuilder<T> setIconType(Material iconType) {
            this.iconType = iconType;
            return this;
        }

        public ElevatorActionVariableBuilder<T> addAction(String action, String description) {
            this.actions.put(action, description);
            return this;
        }

        public ElevatorActionVariableBuilder<T> onClick(Consumer<ElevatorSettingClickContext<T>> onClick) {
            this.onClick = onClick;
            return this;
        }

        public ElevatorActionVariableBuilder<T> allowPerEleCustomization() {
            this.allowPerEleCustomization = true;
            return this;
        }

        protected void validate() throws ElevatorActionBuilderException {
            if(this.defaultValue == null)
                throw new ElevatorActionBuilderException("Default Value is not set");
            if(this.conversionFunction == null)
                throw new ElevatorActionBuilderException("ConversionFunction is not set");
            if(this.alias == null || this.alias.length == 0)
                throw new ElevatorActionBuilderException("Alias is not set");
            if(this.settingName == null)
                throw new ElevatorActionBuilderException("Setting Name is not set");
            if(this.description == null)
                throw new ElevatorActionBuilderException("Description is not set");
            if(this.displayName == null)
                throw new ElevatorActionBuilderException("Display Name is not set");
            if(this.iconType == null)
                throw new ElevatorActionBuilderException("Icon Type is not set");
        }

        protected ElevatorActionVariable<T> build() {
            this.builtGrouping = new ElevatorActionVariable<>(this.defaultValue, this.conversionFunction, this.alias[0], Arrays.copyOfRange(this.alias, 1, this.alias.length));
            return this.builtGrouping;
        }

        protected void setup(ElevatorAction action) throws ElevatorActionBuilderException {
            if(this.builtGrouping == null)
                throw new ElevatorActionBuilderException("Elevator variable was setup for being built");

            ElevatorActionSetting<T> setting = action.mapSetting(this.builtGrouping, this.settingName, this.displayName, this.description, this.iconType, this.allowPerEleCustomization);
            setting.onClick((player, returnMethod, clickEvent, currentValue, setValueMethod) -> {
                this.onClick.accept(new ElevatorSettingClickContext<>(player, returnMethod, clickEvent, currentValue, setValueMethod));
            });
            for(String actionKey : this.actions.keySet())
                setting.addAction(actionKey, this.actions.get(actionKey));
        }

    }

}
