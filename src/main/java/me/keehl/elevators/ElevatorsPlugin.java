package me.keehl.elevators;

import org.bukkit.plugin.java.JavaPlugin;

public class ElevatorsPlugin extends JavaPlugin {

    @Override()
    public void onEnable() {
        Elevators.enable(this);

        ElevatorHooks.buildHooks();
    }

    @Override()
    public void onDisable() {
        Elevators.disable();
    }

}
