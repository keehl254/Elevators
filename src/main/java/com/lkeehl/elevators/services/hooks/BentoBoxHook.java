package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.models.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class BentoBoxHook implements ElevatorHook {

    Flag flag;

    public BentoBoxHook() {

        this.flag = (new Flag.Builder("USE_ELEVATOR", Material.RED_SHULKER_BOX)).mode(Flag.Mode.BASIC).build();
        BentoBox.getInstance().getFlagsManager().registerFlag(this.flag);
        for (Locale objLocale : BentoBox.getInstance().getLocalesManager().getAvailableLocales(true)) {
            BentoBoxLocale locale = BentoBox.getInstance().getLocalesManager().getLanguages().get(objLocale);
            if (!locale.contains("protection.flags.USE_ELEVATOR.name")) {
                locale.set("protection.flags.USE_ELEVATOR.name", "Use elevators");
                locale.set("protection.flags.USE_ELEVATOR.description", "Toggle elevators");
            }
        }
    }

    public boolean isIsland(Location location) {
        return BentoBox.getInstance().getIslands().getIslandAt(location).isPresent();
    }
    @Override
    public boolean canPlayerUseElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        return false;
    }

    @Override
    public ItemStack createIconForElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        return null;
    }
}
