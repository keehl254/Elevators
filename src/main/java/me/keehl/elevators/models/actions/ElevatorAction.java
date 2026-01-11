package me.keehl.elevators.models.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.api.models.*;
import me.keehl.elevators.api.services.*;
import me.keehl.elevators.api.services.interaction.ISimpleDisplay;
import me.keehl.elevators.helpers.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Subst;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ElevatorAction implements IElevatorAction {

    // A regex pattern used to recognize the following format: key: value
    static Pattern subPattern = Pattern.compile("([a-zA-Z]+)=(.*?(?= [a-zA-Z]+=)|.*\\S)");

    private static final IElevatorActionVariable<UUID> keyGrouping = new ElevatorActionVariable<>(null, UUID::fromString, "identifier", "identifier", "i");

    private final JavaPlugin plugin;
    private final IElevatorType elevatorType;

    protected String value;

    private final String key;

    private final String defaultVariableAlias;

    private final Map<IElevatorActionVariable<?>, Object> variableData = new HashMap<>();

    private final List<IElevatorActionVariable<?>> variables;

    private final Map<IElevatorActionVariable<?>, IElevatorActionSetting<?>> settings = new HashMap<>();

    private ItemStack icon;
    private boolean initialized = false;

    static IElevatorVersionService getVersionService() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(IElevatorVersionService.class)).orElseThrow();
    }

    static IElevatorSettingService getSettingService() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(IElevatorSettingService.class)).orElseThrow();
    }

    static IElevatorObstructionService getObstructionService() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(IElevatorObstructionService.class)).orElseThrow();
    }

    static IElevatorHologramService getHologramService() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(IElevatorHologramService.class)).orElseThrow();
    }

    static IElevatorHookService getHookService() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(IElevatorHookService.class)).orElseThrow();
    }

    static IElevatorConfigService getConfigService() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(IElevatorConfigService.class)).orElseThrow();
    }

    static IElevatorDataContainerService getDataContainerService() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(IElevatorDataContainerService.class)).orElseThrow();
    }


    protected ElevatorAction(JavaPlugin plugin, IElevatorType elevatorType, String key, IElevatorActionVariable<?>... variables) {
        this.plugin = plugin;
        this.elevatorType = elevatorType;
        this.key = key;
        this.defaultVariableAlias = variables.length > 0 ? variables[0].getMainAlias() : null;

        this.variables = new ArrayList<>(Arrays.asList(variables));
        this.variables.add(keyGrouping);

        this.icon = ItemStackHelper.createItem(key, Material.EGG, 1);
    }

    public void setIcon(ItemStack item) {
        this.icon = item;
    }

    public final void initialize(String value) {
        if (value.contains(":"))
            value = value.substring(value.indexOf(':') + 1);
        value = value.trim();
        this.value = value;

        boolean defaultVariableSet = false;

        Matcher matcher = subPattern.matcher(this.value);
        while (matcher.find()) {
            String alias = matcher.group(1);
            if (this.calculateVariableFromAlias(alias, matcher.group(2)))
                defaultVariableSet = true;

            value = value.replace(this.value.substring(matcher.start(), matcher.end()), "");
        }

        if (!defaultVariableSet && this.defaultVariableAlias != null)
            this.calculateVariableFromAlias(this.defaultVariableAlias, value);

        for(IElevatorActionVariable<?> grouping : this.variables) {
            if(!this.variableData.containsKey(grouping))
                this.variableData.put(grouping, grouping.getDefaultObject());
        }

        this.initialized = true;
        this.onInitialize(this.value);
        this.initIdentifier();
    }

    public IElevatorType getElevatorType() {
        return this.elevatorType;
    }

    @Subst("test_key")
    public String getKey() {
        return this.key;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder(this.key + ": ");

        for (IElevatorActionVariable<?> variable : this.variableData.keySet()) {
            Object value = this.variableData.get(variable);
            builder.append(variable.getMainAlias());
            builder.append("=");
            builder.append(variable.getStringFromObject(value));
            builder.append(" ");
        }

        return builder.toString().trim();
    }

    public <T> T getVariableValue(IElevatorActionVariable<T> grouping) {
        return this.getVariableValue(grouping, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getVariableValue(IElevatorActionVariable<T> variable, IElevator elevator) {

        if (elevator != null && this.settings.containsKey(variable)) {

            ElevatorActionSetting<T> data = (ElevatorActionSetting<T>) this.settings.get(variable);
            if (!data.isSettingGlobalOnly(elevator))
                return variable.getObjectFromString(data.getIndividualValue(elevator), this);
        }

        if (this.variableData.containsKey(variable))
            return (T) this.variableData.get(variable);
        return variable.getDefaultObject();
    }

    protected Optional<IElevatorActionVariable<?>> getGroupingByAlias(String alias) {
        return this.variables.stream().filter(i -> i.isGroupingAlias(alias)).findFirst();
    }

    public <T> void setGroupingObject(IElevatorActionVariable<T> grouping, T value) {
        if (value.equals(grouping.getDefaultObject()))
            this.variableData.remove(grouping);
        else
            this.variableData.put(grouping, value);

        if(Elevators.getConfigService().isConfigLoaded())
            Elevators.getInstance().saveConfig();
    }

    private boolean calculateVariableFromAlias(String groupingAlias, String groupingValue) {
        String groupingAliasFixed = groupingAlias.trim().toLowerCase();
        String groupingValueFixed = groupingValue.trim().isEmpty() ? null : groupingValue.trim();

        Optional<IElevatorActionVariable<?>> grouping = this.variables.stream().filter(i -> i.isGroupingAlias(groupingAliasFixed)).findFirst();
        grouping.ifPresent(elevatorActionGrouping -> this.variableData.put(elevatorActionGrouping, elevatorActionGrouping.getObjectFromString(groupingValueFixed, this)));

        return grouping.map(elevatorActionGrouping -> elevatorActionGrouping.getMainAlias().equalsIgnoreCase(this.defaultVariableAlias)).orElse(false);
    }

    protected <T> IElevatorActionSetting<T> mapSetting(IElevatorActionVariable<T> grouping, String settingName, String settingDisplayName, String description, Material icon, ChatColor textColor, boolean setupDataStore) {

        if (!this.initialized)
            throw new RuntimeException("Elevator Action Setting mapped prior to initialization. Please move all mapSetting calls to the onInitialize method.");

        ElevatorActionSetting<T> setting = new ElevatorActionSetting<>(this.plugin, this, grouping, settingName, textColor + "" + ChatColor.BOLD + settingDisplayName, description, icon, setupDataStore);
        this.settings.put(grouping, setting);

        this.initIdentifier();
        return setting;
    }

    protected <T> IElevatorActionSetting<T> mapSetting(IElevatorActionVariable<T> grouping, String settingName, String settingDisplayName, String description, Material icon, boolean setupDataStore) {

        if (!this.initialized)
            throw new RuntimeException("Elevator Action Setting mapped prior to initialization. Please move all mapSetting calls to the onInitialize method.");

        ElevatorActionSetting<T> setting = new ElevatorActionSetting<>(this.plugin, this, grouping, settingName, settingDisplayName, description, icon, setupDataStore);
        this.settings.put(grouping, setting);

        this.initIdentifier();
        return setting;
    }

    protected <T> IElevatorActionSetting<T> mapSetting(IElevatorActionVariable<T> grouping, String settingName, String settingDisplayName, String description, Material icon, ChatColor textColor) {
        return mapSetting(grouping, settingName, settingDisplayName, description, icon,textColor, false);
    }

    protected <T> IElevatorActionSetting<T> mapSetting(IElevatorActionVariable<T> grouping, String settingName, String settingDisplayName, String description, Material icon) {
        return mapSetting(grouping, settingName, settingDisplayName, description, icon, false);
    }

    public UUID getIdentifier() {
        return this.getVariableValue(keyGrouping);
    }

    public List<IElevatorActionSetting<?>> getSettings() {
        return new ArrayList<>(this.settings.values());
    }

    public void initIdentifier() {
        UUID currentIdent = this.getVariableValue(keyGrouping);
        if (currentIdent != null)
            return;

        this.setGroupingObject(keyGrouping, UUID.randomUUID());
    }

    public void onStartEditing(Player player, ISimpleDisplay display, IElevator elevator) {}
    public void onStopEditing(Player player, ISimpleDisplay display, IElevator elevator) {}

    public static ElevatorActionBuilder builder(String actionKey) {
        return new ElevatorActionBuilder(actionKey);
    }

    public boolean meetsConditions(IElevatorEventData eventData, Player player) {
        return true;
    }

    protected abstract void onInitialize(String value);

    public abstract void execute(IElevatorEventData eventData, Player player);


}
