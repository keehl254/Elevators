package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.hooks.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.localization.BentoBoxLocale;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
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
    public boolean canPlayerUseElevator(Player player, Elevator elevator, boolean sendMessage) {
        ShulkerBox box = elevator.getShulkerBox();
        Island island = BentoBox.getInstance().getIslands().getIslandAt(box.getLocation()).orElse(null);
        if (island == null)
            return true;
        if (!island.getProtectionBoundingBox().contains(box.getX(), box.getY(), box.getZ()))
            return true;

        User user = BentoBox.getInstance().getPlayers().getUser(player.getUniqueId());

        if(island.isAllowed(user, this.flag))
            return true;

        if(sendMessage)
            user.sendMessage("general.errors.insufficient-rank", TextVariables.RANK, user.getTranslation(BentoBox.getInstance().getRanksManager().getRank(island.getRank(user))));
        return false;
    }


    @Override
    public ItemStack createIconForElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        return null;
    }
}
