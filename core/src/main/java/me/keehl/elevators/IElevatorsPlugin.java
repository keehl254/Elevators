package me.keehl.elevators;

import java.util.logging.Level;

public interface IElevatorsPlugin {

    void log(String message);

    void log(Level level, String message);

    void log(Level level, String message, Throwable throwable);

}
