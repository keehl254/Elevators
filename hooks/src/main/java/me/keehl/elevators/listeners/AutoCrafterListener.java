package me.keehl.elevators.listeners;

import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.ElevatorListenerService;
import me.keehl.elevators.services.ElevatorSettingService;
import me.keehl.elevators.util.InternalElevatorSettingType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.ItemStack;

public class AutoCrafterListener {

    public static void setupListener() {
        ElevatorListenerService.registerEventExecutor(CrafterCraftEvent.class, EventPriority.HIGHEST, AutoCrafterListener::onAutoCraft);
    }

    public static void onAutoCraft(CrafterCraftEvent event) {
        ItemStack result = event.getResult();
        if (result.isEmpty())
            return;
        if (ItemStackHelper.isNotShulkerBox(result.getType()))
            return;

        ElevatorType elevatorType = ElevatorHelper.getElevatorType(result);
        if (elevatorType == null)
            return;

        if (!(boolean) ElevatorSettingService.getElevatorSettingValue(elevatorType, InternalElevatorSettingType.CHECK_PERMS))
            return;

        event.setCancelled(true);
    }

}
