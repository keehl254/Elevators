package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.DataContainerService;
import com.lkeehl.elevators.util.QuadConsumer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ElevatorSetting<T> {

    private final ItemStack iconTemplate;

    private Function<ElevatorType, T> getGlobalCurrentValueFunc = this::getCurrentValueGlobal;
    private Function<Elevator, T> getIndividualCurrentValueFunc = null;
    private BiConsumer<Elevator, T> setIndividualCurrentValueFunc = null;

    private QuadConsumer<Player, ElevatorType, Runnable, T> onClickGlobalConsumer;
    private QuadConsumer<Player, Elevator, Runnable, T> onClickIndividualConsumer;

    public ElevatorSetting(String settingName, String description, Material icon, ChatColor textColor) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.addAll(MessageHelper.formatLore(description, ChatColor.GRAY));

        this.iconTemplate = ItemStackHelper.createItem(textColor + "" + ChatColor.BOLD + settingName, icon, 1, lore);

        this.onClickGlobalConsumer = this::onClickGlobal;
        this.onClickIndividualConsumer = this::onClickIndividual;
    }

    public ElevatorSetting<T> setupDataStore(String settingKey, PersistentDataType<?, T> dataType) {
        NamespacedKey containerKey = DataContainerService.getKeyFromKey("per-ele-" + settingKey, dataType);

        this.getIndividualCurrentValueFunc = elevator -> DataContainerService.getElevatorValue(elevator.getShulkerBox(), containerKey, getGlobalCurrentValueFunc.apply(elevator.getElevatorType()));
        this.setIndividualCurrentValueFunc = (elevator, value) -> {
            if(value == this.getGlobalCurrentValueFunc.apply(elevator.getElevatorType())) // Store as little data as possible. Remove from data-container if default.
                value = null;
            DataContainerService.setElevatorValue(elevator.getShulkerBox(), containerKey, value);
            elevator.getShulkerBox().update();
        };

        return this;
    }

    public boolean canBeEditedIndividually(Elevator elevator) {
        return this.getIndividualCurrentValueFunc != null;
    }

    public ItemStack createIcon(Object value, boolean global) {

        List<String> lore = new ArrayList<>();

        ItemMeta templateMeta = this.iconTemplate.getItemMeta();
        if(templateMeta.hasLore())
            lore.addAll(Objects.requireNonNull(templateMeta.getLore()));

        lore.add("");
        lore.add(ChatColor.GRAY + "Current Value: ");
        if(value instanceof Boolean boolVal)
            lore.add(boolVal ? (ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED") : (ChatColor.RED + "" + ChatColor.BOLD + "DISABLED") );
        else
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + value);

        ItemStack icon = this.iconTemplate.clone();
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setLore(lore);
        icon.setItemMeta(iconMeta);

        return icon;
    }


    public final ElevatorSetting<T> onClickGlobal(QuadConsumer<Player, ElevatorType, Runnable, T> onClickGlobalConsumer) {
        this.onClickGlobalConsumer = onClickGlobalConsumer;
        return this;
    }

    public final ElevatorSetting<T> onClickIndividual(QuadConsumer<Player, Elevator, Runnable, T> onClickIndividualConsumer) {
        this.onClickIndividualConsumer = onClickIndividualConsumer;
        return this;
    }


    public void clickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod) {
        this.onClickGlobalConsumer.accept(player, elevatorType, returnMethod, this.getGlobalCurrentValueFunc.apply(elevatorType));
    }

    public void clickIndividual(Player player, Elevator elevator, Runnable returnMethod) {
        this.onClickIndividualConsumer.accept(player, elevator, returnMethod, this.getIndividualCurrentValueFunc.apply(elevator));
    }


    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, T currentValue) {
        returnMethod.run();
    }

    public void onClickIndividual(Player player, Elevator elevator, Runnable returnMethod, T currentValue) {
        returnMethod.run();
    }



    public T getIndividualElevatorValue(Elevator elevator) {

        T value = null;
        if (this.getIndividualCurrentValueFunc != null)
            value = this.getIndividualCurrentValueFunc.apply(elevator);

        if(value != null)
            return value;

        return this.getGlobalCurrentValueFunc.apply(elevator.getElevatorType());
    }

    public void setIndividualElevatorValue(Elevator elevator, T value) {

        if(this.setIndividualCurrentValueFunc == null)
            throw new RuntimeException("Setting does not have a method for setting individual value.");

        this.setIndividualCurrentValueFunc.accept(elevator, value);
    }


    public ElevatorSetting<T> setGetValueGlobal(Function<ElevatorType, T> currentValueFunc) {
        this.getGlobalCurrentValueFunc = currentValueFunc;
        return this;
    }
    public T getCurrentValueGlobal(ElevatorType elevatorType) {
        return this.getGlobalCurrentValueFunc.apply(elevatorType);
    }

}
