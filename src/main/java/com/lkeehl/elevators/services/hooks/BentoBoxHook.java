package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.models.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.localization.BentoBoxLocale;
import world.bentobox.bentobox.database.objects.Island;

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
        Island island = BentoBox.getInstance().getIslands().getIslandAt(box.getLocation()).orElse(null);
        if (island == null)
            return true;
        if (!island.getProtectionBoundingBox().contains(box.getX(), box.getY(), box.getZ()))
            return true;
        return island.isAllowed(BentoBox.getInstance().getPlayers().getUser(player.getUniqueId()), this.flag);
    }

    @Override
    public ItemStack createIconForElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        return null;
    }
}
