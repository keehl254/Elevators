package me.keehl.elevators.actions;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.models.IElevatorActionSetting;
import me.keehl.elevators.api.models.IElevatorEffect;
import me.keehl.elevators.api.models.IElevatorEventData;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.ExecutionMode;
import me.keehl.elevators.models.actions.ElevatorAction;
import me.keehl.elevators.models.actions.ElevatorActionVariable;
import me.keehl.elevators.services.interaction.PagedDisplay;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class EffectAction extends ElevatorAction {

    private static final ElevatorActionVariable<String> effectNameGrouping = new ElevatorActionVariable<>("CREEPER", i -> i, "name","n");

    public EffectAction(JavaPlugin plugin, IElevatorType elevatorType, String key) {
        super(plugin, elevatorType, key, effectNameGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the effect played.";
        IElevatorActionSetting<String> effectSetting = this.mapSetting(effectNameGrouping, "effect","Effect", desc, Material.CREEPER_HEAD, ChatColor.GOLD, true);
        effectSetting.onClick(this::editEffect);
    }

    @Override
    public void execute(IElevatorEventData eventData, Player player) {
        IElevatorEffect effect = Elevators.getEffectsService().getEffectFromKey(this.getVariableValue(effectNameGrouping, eventData.getOrigin()));
        if(effect == null)
            return;

        ExecutionMode executionMode = Elevators.getConfigService().getRootConfig().getEffectDestination();
        ExecutionMode.executeConsumerWithMode(executionMode, eventData::getElevatorFromExecutionMode, elevator -> effect.playEffect(eventData, elevator));
    }

    private void editEffect(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, String currentValue, Consumer<String> setValueMethod) {
        PagedDisplay<IElevatorEffect> display = new PagedDisplay<>(Elevators.getInstance(), player, Elevators.getEffectsService().getEffects(), "Actions > Action > Effect", returnMethod);
        display.onCreateItem(IElevatorEffect::getIcon);
        display.onClick((item, event, myDisplay) -> {
            setValueMethod.accept(item.getEffectKey());
            myDisplay.returnOrClose();
        });
        display.open();
    }
}
