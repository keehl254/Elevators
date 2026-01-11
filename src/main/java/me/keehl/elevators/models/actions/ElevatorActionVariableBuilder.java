package me.keehl.elevators.models.actions;

import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.api.models.actions.IElevatorActionVariableBuilder;
import me.keehl.elevators.api.models.settings.IElevatorSettingClickContext;
import me.keehl.elevators.util.exceptions.ElevatorActionBuilderException;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ElevatorActionVariableBuilder<T> implements IElevatorActionVariableBuilder<T> {

        protected T defaultValue;
        protected Function<String, T> conversionFunction;
        protected String[] alias;
        protected String settingName;

        protected String description = "";
        protected String displayName;
        protected Material iconType;

        protected boolean allowPerEleCustomization = false;

        protected Consumer<IElevatorSettingClickContext<T>> onClick = IElevatorSettingClickContext::close;

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

        public ElevatorActionVariableBuilder<T> onClick(Consumer<IElevatorSettingClickContext<T>> onClick) {
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

            ElevatorActionSetting<T> setting = (ElevatorActionSetting<T>) action.mapSetting(this.builtGrouping, this.settingName, this.displayName, this.description, this.iconType, this.allowPerEleCustomization);
            setting.onClick((player, returnMethod, clickEvent, currentValue, setValueMethod) -> {
                this.onClick.accept(new ElevatorSettingClickContext<>(player, returnMethod, clickEvent, currentValue, setValueMethod));
            });
            for(String actionKey : this.actions.keySet()) {
                setting.addAction(actionKey, this.actions.get(actionKey));
            }
        }

    }