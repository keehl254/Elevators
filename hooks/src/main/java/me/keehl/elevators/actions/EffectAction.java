package me.keehl.elevators.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.models.*;
import me.keehl.elevators.services.ElevatorEffectService;
import me.keehl.elevators.services.interaction.PagedDisplay;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class EffectAction extends ElevatorAction {

    private static final ElevatorActionVariable<String> effectNameGrouping = new ElevatorActionVariable<>("CREEPER", i -> i, "name","n");

    public EffectAction(JavaPlugin plugin, ElevatorType elevatorType, String key) {
        super(plugin, elevatorType, key, effectNameGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the effect played.";
        ElevatorActionSetting<String> effectSetting = this.mapSetting(effectNameGrouping, "effect","Effect", desc, Material.CREEPER_HEAD, ChatColor.GOLD, true);
        effectSetting.onClick(this::editEffect);
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        ElevatorEffect effect = ElevatorEffectService.getEffectFromKey(this.getVariableValue(effectNameGrouping, eventData.getOrigin()));
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
