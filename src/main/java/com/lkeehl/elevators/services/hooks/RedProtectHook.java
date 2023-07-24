package com.lkeehl.elevators.services.hooks;

import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import com.lkeehl.elevators.models.ElevatorHook;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RedProtectHook implements ElevatorHook {

    private RedProtectAPI redProtect;

    public RedProtectHook() {
        this.redProtect = RedProtect.get().getAPI();

        this.redProtect.addFlag("use-elevators", true, false);
    }

    @Override
    public boolean canPlayerUseElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        Region region = redProtect.getRegion(box.getLocation());
        return (region == null || region.getFlagBool("use-elevators"));
    }

    @Override
    public ItemStack createIconForElevator(Player player, ShulkerBox box, ElevatorType elevatorType) {
        return null;
    }
}
