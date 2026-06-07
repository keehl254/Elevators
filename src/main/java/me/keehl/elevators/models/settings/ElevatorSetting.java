package me.keehl.elevators.models.settings;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorSetting;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.services.configs.versions.configv5_2_0.ConfigSettings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class ElevatorSetting<T> implements IElevatorSetting<T> {

    private final JavaPlugin plugin;

    protected final List<String> comments;
    protected final String settingName;
    protected final ItemStack iconTemplate;

    private NamespacedKey containerKey;

    private final Map<String, String> actions = new HashMap<>();

    public ElevatorSetting(JavaPlugin plugin, @Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingName, ItemStack icon) {
        this.plugin = plugin;
        this.settingName = settingName;
        this.iconTemplate = icon.clone();

        this.comments = new ArrayList<>();
        if (!plugin.getName().equalsIgnoreCase(Elevators.getInstance().getName()))
            this.comments.add("Setting provided by the plugin: " + plugin.getName());
    }

    public ElevatorSetting(JavaPlugin plugin, @Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingName, String settingDisplayName, String description, Material icon) {
        this(plugin, settingName, ItemStackHelper.createItem(settingDisplayName, icon, 1, MessageHelper.formatLore(description, ChatColor.GRAY)));
    }

    public ElevatorSetting(JavaPlugin plugin, @Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingName, String settingDisplayName, String description, Material icon, ChatColor textColor) {
        this(plugin, settingName, textColor + "" + ChatColor.BOLD + settingDisplayName, description, icon);
    }

    public @NotNull IElevatorSetting<T> addAction(@NotNull String action, @NotNull String description) {
        Objects.requireNonNull(action);
        Objects.requireNonNull(description);

        this.actions.put(action, description);
        return this;
    }

    protected IElevatorSetting<T> setupDataStore(String settingKey, PersistentDataType<?, T> dataType) {
        this.containerKey = Elevators.getDataContainerService().getKeyFromKey("per-ele-" + settingKey, dataType);

        return this;
    }

    public final boolean isSettingGlobalOnly(@NotNull IElevator elevator) {
        Objects.requireNonNull(elevator);

        return elevator.getSnapshotElevatorType().getDisabledSettings().contains(this.settingName) || !canBeEditedIndividually(elevator);
    }

    public abstract boolean canBeEditedIndividually(@NotNull IElevator elevator);

    public @NotNull ItemStack createIcon(@NotNull Object value, boolean global) {
        Objects.requireNonNull(value);

        List<String> lore = new ArrayList<>();

        ItemMeta templateMeta = this.iconTemplate.getItemMeta();
        if (templateMeta.hasLore())
            lore.addAll(Objects.requireNonNull(templateMeta.getLore()));

        lore.add("");
        lore.add(ChatColor.GRAY + "Current Value: ");
        if (value instanceof Boolean)
            lore.add((boolean) value ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED"));
        else if(value instanceof ILocaleComponent localeComponent)
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + localeComponent.toLegacyText());
        else
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + MessageHelper.formatLineColors(value.toString()));

        if (!this.actions.isEmpty()) {
            lore.add("");
            this.actions.forEach((action, description) ->
                    lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + action + ": " + ChatColor.GRAY + description)
            );
        }

        ItemStack icon = this.iconTemplate.clone();
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setLore(lore);
        icon.setItemMeta(iconMeta);

        return icon;
    }

    public final void clickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(elevatorType);
        Objects.requireNonNull(returnMethod);
        Objects.requireNonNull(clickEvent);

        this.onClickGlobal(player, elevatorType, returnMethod, clickEvent, this.getGlobalValue(elevatorType));
    }

    public final void clickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(elevator);
        Objects.requireNonNull(returnMethod);
        Objects.requireNonNull(clickEvent);

        this.onClickIndividual(player, elevator, returnMethod, clickEvent, this.getIndividualValue(elevator));
    }

    public abstract void onClickGlobal(@NotNull Player player, @NotNull IElevatorType elevatorType, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull T currentValue);

    public abstract void onClickIndividual(@NotNull Player player, @NotNull IElevator elevator, @NotNull Runnable returnMethod, @NotNull InventoryClickEvent clickEvent, @NotNull T currentValue);

    public abstract @NotNull T getGlobalValue(@NotNull IElevatorType elevatorType);


    public final @NotNull T getIndividualValue(@NotNull IElevator elevator) {
        Objects.requireNonNull(elevator);

        if (!this.isSettingGlobalOnly(elevator) && this.containerKey != null) {
            T value = Elevators.getDataContainerService().getElevatorValue(elevator.getShulkerBox(), this.containerKey, this.getGlobalValue(elevator.getSnapshotElevatorType()));
            if (value != null)
                return value;
        }

        return this.getGlobalValue(elevator.getSnapshotElevatorType());
    }

    public void setIndividualValue(@NotNull IElevator elevator, @NotNull T value) {
        Objects.requireNonNull(elevator);
        Objects.requireNonNull(value);

        if (this.containerKey == null)
            throw new IllegalStateException("Setting does not support individual overrides (datastore not configured).");

        // Store as little data as possible. Remove from data-container if it's the default.
        if (Objects.equals(value, this.getGlobalValue(elevator.getSnapshotElevatorType())))
            value = null;


        Elevators.getDataContainerService().setElevatorValue(elevator.getShulkerBox(), this.containerKey, value);
        elevator.getShulkerBox().update();
    }

    public final void applyToElevatorSettings(IElevatorType elevatorType, ConfigSettings settings) {
        if (!(this instanceof BuilderElevatorSetting))
            return;
        settings.setData(this.settingName, this.getGlobalValue(elevatorType), this.getComments());
    }

    public static <T> ElevatorSettingBuilder<T> builder(@Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingKey, T defaultValue, PersistentDataType<?, T> dataType) {
        return new ElevatorSettingBuilder<>(settingKey, defaultValue, dataType);
    }

    public @NotNull String getSettingName() {
        return this.settingName;
    }

    public @NotNull JavaPlugin getPlugin() {
        return this.plugin;
    }

    private List<String> getComments() {
        return new ArrayList<>(this.comments);
    }

}
