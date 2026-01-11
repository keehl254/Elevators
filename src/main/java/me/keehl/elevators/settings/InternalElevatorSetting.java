package me.keehl.elevators.settings;

import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.settings.ElevatorSetting;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;

abstract class InternalElevatorSetting<T> extends ElevatorSetting<T> {

    protected InternalElevatorSetting(JavaPlugin plugin, @Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingName, ItemStack icon) {
        super(plugin, settingName, icon);
    }

    protected InternalElevatorSetting(JavaPlugin plugin, @Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingName, String settingDisplayName, String description, Material icon) {
        this(plugin, settingName, ItemStackHelper.createItem(settingDisplayName, icon, 1, MessageHelper.formatLore(description, ChatColor.GRAY)));
    }

    protected InternalElevatorSetting(JavaPlugin plugin, @Subst("test_key") @Pattern("[a-z0-9/._-]+") String settingName, String settingDisplayName, String description, Material icon, ChatColor textColor) {
        this(plugin, settingName, textColor + "" + ChatColor.BOLD + settingDisplayName, description, icon);
    }
}
