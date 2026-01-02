package me.keehl.elevators.actions;

import me.keehl.elevators.actions.settings.ElevatorActionSetting;
import me.keehl.elevators.models.ElevatorAction;
import me.keehl.elevators.models.ElevatorActionVariable;
import me.keehl.elevators.models.ElevatorEventData;
import me.keehl.elevators.models.ElevatorType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class ChargeExpAction extends ElevatorAction {

    private static final ElevatorActionVariable<Integer> expLevelGrouping = new ElevatorActionVariable<>(0, Integer::parseInt, "level","lvl","l");
    private static final ElevatorActionVariable<Integer> expGrouping = new ElevatorActionVariable<>(0, Integer::parseInt, "exp","e", "xp","x");

    public ChargeExpAction(JavaPlugin plugin, ElevatorType elevatorType, String key) {
        super(plugin, elevatorType, key, expLevelGrouping, expGrouping);
    }

    @Override
    protected void onInitialize(String value) {
        String desc = "This option controls the exp levels charged to the player";
        ElevatorActionSetting<Integer> levelSetting = this.mapSetting(expLevelGrouping, "level","Levels", desc, Material.ENCHANTED_BOOK, ChatColor.GOLD, true);
        levelSetting.onClick(this::editLevelOrExp);
        levelSetting.addAction("Left Click", "Increase Level");
        levelSetting.addAction("Right Click", "Decrease Level");
        levelSetting.addAction("Shift Click", "Reset Level");


        desc = "This option controls the xp charged to the player";
        ElevatorActionSetting<Integer> expSetting = this.mapSetting(expGrouping, "exp","XP", desc, Material.EXPERIENCE_BOTTLE, ChatColor.YELLOW, true);
        expSetting.onClick(this::editLevelOrExp);
        expSetting.addAction("Left Click", "Increase XP");
        expSetting.addAction("Right Click", "Decrease XP");
        expSetting.addAction("Shift Click", "Reset XP");
    }

    private static int getExpToLevelUp(int level){
        if(level <= 15){
            return 2*level+7;
        } else if(level <= 30){
            return 5*level-38;
        } else {
            return 9*level-158;
        }
    }

    private static int getExpAtLevel(int level){
        if(level <= 16){
            return (int) (Math.pow(level,2) + 6*level);
        } else if(level <= 31){
            return (int) (2.5*Math.pow(level,2) - 40.5*level + 360.0);
        } else {
            return (int) (4.5*Math.pow(level,2) - 162.5*level + 2220.0);
        }
    }

    private static int getPlayerExp(Player player){
        int exp = 0;
        int level = player.getLevel();

        exp += getExpAtLevel(level);

        exp += Math.round(getExpToLevelUp(level) * player.getExp());

        return exp;
    }

    private static void changePlayerExp(Player player, int exp){
        int currentExp = getPlayerExp(player);

        player.setExp(0);
        player.setLevel(0);

        int newExp = currentExp + exp;
        player.giveExp(newExp);
    }

    @Override()
    public boolean meetsConditions(ElevatorEventData eventData, Player player) {
        if(player.getGameMode() == GameMode.CREATIVE)
            return true;

        int requiredLevel = this.getVariableValue(expLevelGrouping, eventData.getOrigin());
        int requiredExp = this.getVariableValue(expGrouping, eventData.getOrigin());

        int totalRequiredEXP = getExpAtLevel(requiredLevel) + requiredExp;

        return getPlayerExp(player) >= totalRequiredEXP;
    }

    @Override
    public void execute(ElevatorEventData eventData, Player player) {
        if(player.getGameMode() == GameMode.CREATIVE)
            return;

        int requiredLevel = this.getVariableValue(expLevelGrouping, eventData.getOrigin());
        int requiredExp = this.getVariableValue(expGrouping, eventData.getOrigin());

        int totalRequiredEXP = getExpAtLevel(requiredLevel) + requiredExp;
        changePlayerExp(player, -totalRequiredEXP);
    }

    public void editLevelOrExp(Player player, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue, Consumer<Integer> setValueMethod) {
        if(clickEvent.isShiftClick()) {
            setValueMethod.accept(0);
            returnMethod.run();
            return;
        }

        int newValue = currentValue + (clickEvent.isLeftClick() ? 1 : -1);
        newValue = Math.min(Math.max(newValue, 0), 500);
        setValueMethod.accept(newValue);
        returnMethod.run();
    }

}
