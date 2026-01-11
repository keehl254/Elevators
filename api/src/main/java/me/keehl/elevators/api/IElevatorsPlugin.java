package me.keehl.elevators.api;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface IElevatorsPlugin {

    void log(Object message);

    void log(Level level, Object message);

    void log(Level level, Object message, Throwable throwable);

    Logger getLogger();

}
