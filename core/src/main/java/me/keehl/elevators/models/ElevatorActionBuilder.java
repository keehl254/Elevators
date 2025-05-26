package me.keehl.elevators.models;

import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.services.ElevatorActionService;
import me.keehl.elevators.util.PentaConsumer;
import me.keehl.elevators.util.TriConsumer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ElevatorActionBuilder {

    private List<ElevatorActionVariableBuilder<?>> groupings = new ArrayList<>();

    private final String actionKey;
    private TriConsumer<ElevatorGroupingHolder, ElevatorEventData, Player> executeConsumer;
    private Runnable onInit;

    public ElevatorActionBuilder(String actionKey) {
        this.actionKey = actionKey;
    }

    public ElevatorActionBuilder onExecute(TriConsumer<ElevatorGroupingHolder, ElevatorEventData, Player> executeConsumer) {
        this.executeConsumer = executeConsumer;
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

    public void register(ItemStack icon) {
        Function<ElevatorType, ElevatorAction> buildAction = type -> {

            Map<ElevatorActionVariableBuilder<?>, ElevatorActionVariable<?>> varBuilderXGroups = new HashMap<>();
            for(ElevatorActionVariableBuilder<?> builder : this.groupings)
                varBuilderXGroups.put(builder, builder.build());

            return new BuilderElevatorAction(this, type, varBuilderXGroups);
        };

        ElevatorActionService.registerElevatorAction(this.actionKey, buildAction, icon);
    }

    public void register(String chatColor,  String displayName, Material itemType) {
        this.register(ItemStackHelper.createItem(chatColor + ChatColor.BOLD + displayName, itemType, 1));
    }

    public static class BuilderElevatorAction extends ElevatorAction {


        private final ElevatorActionBuilder builder;
        private final Map<ElevatorActionVariableBuilder<?>, ElevatorActionVariable<?>> variableBuilders;

        protected BuilderElevatorAction(ElevatorActionBuilder builder, ElevatorType elevatorType, Map<ElevatorActionVariableBuilder<?>, ElevatorActionVariable<?>> variableBuilders) {
            super(elevatorType, builder.actionKey, variableBuilders.values().toArray(new ElevatorActionVariable<?>[]{}));

            this.builder = builder;
            this.variableBuilders = variableBuilders;
        }

        @Override
        protected void onInitialize(String value) {
            for(ElevatorActionVariableBuilder<?> variableBuilder : this.variableBuilders.keySet())
                variableBuilder.setup(this);
            this.builder.onInit.run();
        }

        @Override
        public void execute(ElevatorEventData eventData, Player player) {
            ElevatorGroupingHolder holder = new ElevatorGroupingHolder();
            holder.action = this;
            holder.elevator = eventData.getOrigin();

            this.builder.executeConsumer.accept(holder, eventData, player);
        }
    }

    public static class ElevatorGroupingHolder {

        private ElevatorAction action;
        private Elevator elevator;

        @SuppressWarnings("unchecked")
        public <T> T getVariable(String alias) {
            Optional<ElevatorActionVariable<?>> groupingOptional = this.action.getGroupingByAlias(alias);
            if(!groupingOptional.isPresent())
                throw new RuntimeException("Attempt to pull Elevator Action Variable with alias that was not setup: " + this.action.getKey() + " -> " + alias);
            return (T) this.action.getVariableValue(groupingOptional.get(), this.elevator);
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

        protected PentaConsumer<Player, Runnable, InventoryClickEvent, T, Consumer<T>> onClick = (player, returnMethod, event, currentValue, setMethod)-> returnMethod.run();

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

        public ElevatorActionVariableBuilder<T> onClick(PentaConsumer<Player, Runnable, InventoryClickEvent, T, Consumer<T>> onClick) {
            this.onClick = onClick;
            return this;
        }

        public ElevatorActionVariableBuilder<T> allowPerEleCustomization() {
            this.allowPerEleCustomization = true;
            return this;
        }

        protected void validate() {
            if(this.defaultValue == null)
                throw new RuntimeException("Default Value is not set");
            if(this.conversionFunction == null)
                throw new RuntimeException("ConversionFunction is not set");
            if(this.alias == null || this.alias.length == 0)
                throw new RuntimeException("Alias is not set");
            if(this.settingName == null)
                throw new RuntimeException("Setting Name is not set");
            if(this.description == null)
                throw new RuntimeException("Description is not set");
            if(this.displayName == null)
                throw new RuntimeException("Display Name is not set");
            if(this.iconType == null)
                throw new RuntimeException("Icon Type is not set");
        }

        protected ElevatorActionVariable<T> build() {
            this.builtGrouping = new ElevatorActionVariable<>(this.defaultValue, this.conversionFunction, this.alias[0], Arrays.copyOfRange(this.alias, 1, this.alias.length));
            return this.builtGrouping;
        }

        protected void setup(ElevatorAction action) {
            if(this.builtGrouping == null)
                throw new RuntimeException("Elevator variable was setup for being built");

            ElevatorActionSetting<T> setting = action.mapSetting(this.builtGrouping, this.settingName, this.displayName, this.description, this.iconType, this.allowPerEleCustomization);
            setting.onClick(this.onClick);
        }

    }

}
