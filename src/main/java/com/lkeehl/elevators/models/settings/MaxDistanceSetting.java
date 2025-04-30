package com.lkeehl.elevators.models.settings;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.ElevatorType;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.SignGUIBuilder;
import de.rapha149.signgui.exception.SignGUIVersionException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class MaxDistanceSetting extends ElevatorSetting<Integer> {

    public MaxDistanceSetting() {
        super("change-max-distance","Max Distance", "This controls the number of blocks that the origin elevator will search for a destination elevator.", Material.MINECART, ChatColor.DARK_GREEN);
        this.setGetValueGlobal(ElevatorType::getMaxDistanceAllowedBetweenElevators);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue) {

        SignGUIBuilder builder = null;
        try {
            builder = SignGUI.builder();
            builder.setLines(currentValue + "", ChatColor.BOLD + "^^^^^^^^", "Enter new", "max distance");
            builder.setHandler((p, result) -> {
                String maxDistanceStr = result.getLineWithoutColor(0).trim();

                int maxDistance = currentValue;
                if (maxDistanceStr.equalsIgnoreCase("infinite"))
                    maxDistance = -1;
                else {
                    try {
                        maxDistance = Integer.parseInt(maxDistanceStr);
                        if (maxDistance < 0 && maxDistance != -1)
                            throw new Exception("Invalid entry");
                    } catch (Exception ignored) {
                        return List.of(SignGUIAction.displayNewLines(currentValue + "", ChatColor.BOLD + "^^^^^^^^", "Enter new", "max distance"));
                    }
                }

                final int finalDistance = maxDistance;
                return List.of(SignGUIAction.runSync(Elevators.getInstance(), () -> {
                    elevatorType.setMaxDistanceAllowedBetweenElevators(finalDistance);
                    returnMethod.run();
                }));
            });
            builder.build().open(player);
        } catch (SignGUIVersionException e) {
            throw new RuntimeException(e);
        }


    }
}
