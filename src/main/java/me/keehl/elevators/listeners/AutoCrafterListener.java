package me.keehl.elevators.listeners;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.InternalElevatorSettingType;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

public class AutoCrafterListener {

    @SuppressWarnings("unchecked")
    public static void setupListener() {
        try {
            Class<? extends Event> crafterCraftEventClass = (Class<? extends Event>) Class.forName("org.bukkit.event.block.CrafterCraftEvent");

            Elevators.getListenerService().registerEventExecutor(crafterCraftEventClass, EventPriority.HIGHEST, (event) -> {

                if(!(event instanceof Cancellable cancellable))
                    return;

                try {

                    ItemStack result = (ItemStack) event.getClass().getMethod("getResult").invoke(event);
                    if(result.getAmount() == 0 || result.getType().isEmpty())
                        return;
                    if (ItemStackHelper.isNotShulkerBox(result.getType()))
                        return;
                    IElevatorType elevatorType = ElevatorHelper.getElevatorType(result);
                    if (elevatorType == null)
                        return;

                    if (!(boolean) Elevators.getSettingService().getElevatorSettingValue(elevatorType, InternalElevatorSettingType.CHECK_PERMS))
                        return;

                    cancellable.setCancelled(true);
                } catch (Exception e) {
                    ElevatorsAPI.log(Level.SEVERE, "Error in AutoCrafterListener: ", e);
                }

            });
        } catch (Throwable e) {
            ElevatorsAPI.log(Level.SEVERE, "Error in AutoCrafterListener: ", e);
        }
    }

}
