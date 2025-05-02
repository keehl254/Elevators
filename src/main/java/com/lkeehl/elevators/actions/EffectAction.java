package com.lkeehl.elevators.actions;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.actions.settings.ElevatorActionSetting;
import com.lkeehl.elevators.models.*;
import com.lkeehl.elevators.services.ElevatorEffectService;
import com.lkeehl.elevators.services.interaction.PagedDisplay;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class EffectAction extends ElevatorAction {

    private static final ElevatorActionGrouping<String> effectNameGrouping = new ElevatorActionGrouping<>("CREEPER", i -> i, "name","n");

    public EffectAction(ElevatorType elevatorType) {
        super(elevatorType, "effect","name", effectNameGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the effect played.";
        ElevatorActionSetting<String> effectSetting = this.mapSetting(effectNameGrouping, "effect","Effect", desc, Material.CREEPER_HEAD, ChatColor.GOLD);
        effectSetting.setupDataStore("effect", PersistentDataType.STRING);
        effectSetting.onClick(this::editEffect);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        ElevatorEffect effect = ElevatorEffectService.getEffectFromKey(this.getGroupingObject(effectNameGrouping, eventData.getOrigin()));
        if(effect == null)
            return;

        effect.playEffect(eventData);
    }

    private void editEffect(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        PagedDisplay<ElevatorEffect> display = new PagedDisplay<>(Elevators.getInstance(), player, ElevatorEffectService.getEffects(), "Actions > Action > Effect", returnMethod);
        display.onCreateItem(ElevatorEffect::getIcon);
        display.onClick((item, event, myDisplay) -> {
            setValueMethod.accept(item.getEffectKey());
            myDisplay.returnOrClose();
        });
        display.open();
    }
}
