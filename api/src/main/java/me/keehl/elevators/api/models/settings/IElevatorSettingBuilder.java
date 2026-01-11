package me.keehl.elevators.api.models.settings;

import me.keehl.elevators.api.models.IElevator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IElevatorSettingBuilder<T> {

    IBuilderElevatorSetting<T> register(JavaPlugin plugin, ItemStack icon);

    void register(JavaPlugin plugin, String chatColor, String displayName, Material itemType);

    IElevatorSettingBuilder<T> addAction(String trigger, String action);

    IElevatorSettingBuilder<T> addComment(String comment);

    IElevatorSettingBuilder<T> setCanEditIndividually(Function<IElevator, Boolean> canEditFunc);

    IElevatorSettingBuilder<T> allowPerEleCustomization();

    IElevatorSettingBuilder<T> onClick(Consumer<IElevatorSettingClickContext<T>> onClick);

}
