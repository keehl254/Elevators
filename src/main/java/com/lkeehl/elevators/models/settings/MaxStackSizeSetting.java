package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.ElevatorType;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.SignGUIBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MaxStackSizeSetting extends ElevatorSetting<Integer> {

    public MaxStackSizeSetting() {
        super("Max Stack Size", "This controls the maximum stack size of elevator item stacks.", Material.COMPARATOR, ChatColor.YELLOW, false);
        this.setGetValueGlobal(ElevatorType::getMaxStackSize);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, Integer currentValue) {
        SignGUIBuilder builder = SignGUI.builder();
        builder.setLines(currentValue + "", ChatColor.BOLD + "^^^^^^^^", "Enter new", "max stack size");
        builder.setHandler((p, result) -> {
            String strValue = result.getLineWithoutColor(0).trim();

            int intValue;
            try {
                intValue = Integer.parseInt(strValue);
                if (intValue < 1 || intValue > 64)
                    throw new Exception("Invalid entry");
            } catch (Exception ignored) {
                return List.of(SignGUIAction.displayNewLines(currentValue + "", ChatColor.BOLD + "^^^^^^^^", "Enter new", "max stack size"));
            }

            final int finalIntValue = intValue;
            return List.of(SignGUIAction.runSync(Elevators.getInstance(), () -> {
                elevatorType.setMaxStackSize(finalIntValue);
                returnMethod.run();
            }));
        });
        builder.build().open(player);
    }

}
