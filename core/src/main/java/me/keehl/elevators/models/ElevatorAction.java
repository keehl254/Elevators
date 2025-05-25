package me.keehl.elevators.models;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.services.ElevatorConfigService;
import me.keehl.elevators.services.interaction.SimpleDisplay;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ElevatorAction {

    // A regex pattern used to recognize the following format: key: value
    static Pattern subPattern = Pattern.compile("([a-zA-Z]+)=(.*?(?= [a-zA-Z]+=)|.*\\S)");

    private static final ElevatorActionGrouping<UUID> keyGrouping = new ElevatorActionGrouping<>(null, UUID::fromString, "identifier", "identifier", "i");

    private final ElevatorType elevatorType;

    protected String value;

    private final String key;

    private final String defaultGroupingAlias;

    private final Map<ElevatorActionGrouping<?>, Object> groupingData = new HashMap<>();

    private final List<ElevatorActionGrouping<?>> groupings;

    private final Map<ElevatorActionGrouping<?>, ElevatorActionSetting<?>> settings = new HashMap<>();

    private ItemStack icon;
    private boolean initialized = false;


    protected ElevatorAction(ElevatorType elevatorType, String key, String defaultGroupingAlias, ElevatorActionGrouping<?>... groupings) {
        this.elevatorType = elevatorType;
        this.key = key;
        this.defaultGroupingAlias = defaultGroupingAlias;

        this.groupings = new ArrayList<>(Arrays.asList(groupings));
        this.groupings.add(keyGrouping);

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

        boolean defaultGroupingSet = false;

        Matcher matcher = subPattern.matcher(this.value);
        while (matcher.find()) {
            String alias = matcher.group(1);
            if (this.calculateGroupingFromAlias(alias, matcher.group(2)))
                defaultGroupingSet = true;

            value = value.replace(this.value.substring(matcher.start(), matcher.end()), "");
        }

        if (!defaultGroupingSet)
            this.calculateGroupingFromAlias(this.defaultGroupingAlias, value);

        for(ElevatorActionGrouping<?> grouping : this.groupings) {
            if(!this.groupingData.containsKey(grouping))
                this.groupingData.put(grouping, grouping.getDefaultObject());
        }

        this.initialized = true;
        this.onInitialize(this.value);
    }

    public ElevatorType getElevatorType() {
        return this.elevatorType;
    }

    public String getKey() {
        return this.key;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder(this.key + ": ");

        for (ElevatorActionGrouping<?> grouping : this.groupingData.keySet()) {
            Object value = this.groupingData.get(grouping);
            builder.append(grouping.getMainAlias());
            builder.append("=");
            builder.append(grouping.getStringFromObject(value));
            builder.append(" ");
        }

        return builder.toString().trim();
    }

    public <T> T getGroupingObject(ElevatorActionGrouping<T> grouping) {
        return this.getGroupingObject(grouping, null);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getGroupingObject(ElevatorActionGrouping<T> grouping, Elevator elevator) {

        if (elevator != null && this.settings.containsKey(grouping)) {

            ElevatorActionSetting<T> data = (ElevatorActionSetting<T>) this.settings.get(grouping);
            if (data.canBeEditedIndividually(elevator))
                return grouping.getObjectFromString(data.getIndividualElevatorValue(elevator), this);
        }

        if (this.groupingData.containsKey(grouping))
            return (T) this.groupingData.get(grouping);
        return grouping.getDefaultObject();
    }

    public <T> void setGroupingObject(ElevatorActionGrouping<T> grouping, T value) {
        if (value.equals(grouping.getDefaultObject()))
            this.groupingData.remove(grouping);
        else
            this.groupingData.put(grouping, value);

        if(ElevatorConfigService.isConfigLoaded())
            Elevators.getInstance().saveConfig();
    }

    private boolean calculateGroupingFromAlias(String groupingAlias, String groupingValue) {
        String groupingAliasFixed = groupingAlias.trim().toLowerCase();
        String groupingValueFixed = groupingValue.trim().isEmpty() ? null : groupingValue.trim();

        Optional<ElevatorActionGrouping<?>> grouping = this.groupings.stream().filter(i -> i.isGroupingAlias(groupingAliasFixed)).findFirst();
        grouping.ifPresent(elevatorActionGrouping -> this.groupingData.put(elevatorActionGrouping, elevatorActionGrouping.getObjectFromString(groupingValueFixed, this)));

        return grouping.map(elevatorActionGrouping -> elevatorActionGrouping.getMainAlias().equalsIgnoreCase(this.defaultGroupingAlias)).orElse(false);
    }

    protected <T> ElevatorActionSetting<T> mapSetting(ElevatorActionGrouping<T> grouping, String settingName, String settingDisplayName, String description, Material icon, ChatColor textColor) {

        if (!this.initialized)
            throw new RuntimeException("Elevator Action Setting mapped prior to initialization. Please move all mapSetting calls to the onInitialize method.");

        ElevatorActionSetting<T> setting = new ElevatorActionSetting<>(this, grouping, settingName, settingDisplayName, description, icon, textColor);
        this.settings.put(grouping, setting);

        this.initIdentifier();
        return setting;
    }

    public UUID getIdentifier() {
        return this.getGroupingObject(keyGrouping);
    }

    public List<ElevatorActionSetting<?>> getSettings() {
        return new ArrayList<>(this.settings.values());
    }

    public void initIdentifier() {
        UUID currentIdent = this.getGroupingObject(keyGrouping);
        if (currentIdent != null)
            return;

        this.setGroupingObject(keyGrouping, UUID.randomUUID());
    }

    public void onStartEditing(Player player, SimpleDisplay display, Elevator elevator) {}
    public void onStopEditing(Player player, SimpleDisplay display, Elevator elevator) {}

    protected abstract void onInitialize(String value);

    public abstract void execute(ElevatorEventData eventData, Player player);


}
