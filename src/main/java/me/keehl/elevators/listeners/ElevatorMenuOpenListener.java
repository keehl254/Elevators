package me.keehl.elevators.listeners;

import me.keehl.elevators.events.ElevatorMenuOpenEvent;
import me.keehl.elevators.menus.interact.InteractMenu;

public class ElevatorMenuOpenListener {

    public static void onInteractMenuOpen(ElevatorMenuOpenEvent event) {
        if(event.isCancelled())
            return;

        InteractMenu.openInteractMenu(event.getPlayer(), event.getElevator());
    }

}
