package me.keehl.elevators;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomLogger extends Logger {

    protected CustomLogger(Logger logger) {
        super(logger.getName(), logger.getResourceBundleName());
    }

    @Override()
    public void log(Level level, String message) {
        Elevators.log(level, message);
    }

    @Override()
    public void log(Level level, String message, Throwable throwable) {
        Elevators.log(level, message, throwable);
    }


}
