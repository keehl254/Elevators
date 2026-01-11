package me.keehl.elevators;

import me.keehl.elevators.api.ElevatorsAPI;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomLogger extends Logger {

    protected CustomLogger(Logger logger) {
        super(logger.getName(), logger.getResourceBundleName());
    }

    @Override()
    public void log(Level level, String message) {
        ElevatorsAPI.log(level, message);
    }

    @Override()
    public void log(Level level, String message, Throwable throwable) {
        ElevatorsAPI.log(level, message, throwable);
    }


}
