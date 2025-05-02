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

import java.util.List;

public class MaxSolidBlocksSetting extends ElevatorSetting<Integer> {

    public MaxSolidBlocksSetting() {
        super("change-max-solid-blocks","Max Solid Blocks", "This controls the maximum number of solid blocks that can be between an origin and destination elevator.", Material.IRON_BLOCK, ChatColor.RED);
        this.setGetValueGlobal(ElevatorType::getMaxSolidBlocksAllowedBetweenElevators);
    }

    @Override
    public void onClickGlobal(Player player, ElevatorType elevatorType, Runnable returnMethod, InventoryClickEvent clickEvent, Integer currentValue) {

        SignGUIBuilder builder = null;
        try {
            builder = SignGUI.builder();
            builder.setLines(currentValue+"", ChatColor.BOLD+"^^^^^^^^", "Enter new", "max solid blocks");
            builder.setHandler((p, result) -> {
                String maxSolidBlocksStr = result.getLineWithoutColor(0).trim();

                int maxSolidBlocks = currentValue;
                if(maxSolidBlocksStr.equalsIgnoreCase("infinite"))
                    maxSolidBlocks = -1;
                else {
                    try {
                        maxSolidBlocks = Integer.parseInt(maxSolidBlocksStr);
                        if(maxSolidBlocks < 0 && maxSolidBlocks != -1)
                            throw new Exception("Invalid entry");
                    } catch (Exception ignored) {
                        return List.of(SignGUIAction.displayNewLines(currentValue+"", ChatColor.BOLD+"^^^^^^^^", "Enter new", "max solid blocks"));
                    }
                }

                final int finalMaxSolidBlocks = maxSolidBlocks;
                return List.of(SignGUIAction.runSync(Elevators.getInstance(), () -> {
                    elevatorType.setMaxSolidBlocksAllowedBetweenElevators(finalMaxSolidBlocks);
                    returnMethod.run();
                }));
            });
            builder.build().open(player);
        } catch (SignGUIVersionException e) {
            throw new RuntimeException(e);
        }

    }

}
