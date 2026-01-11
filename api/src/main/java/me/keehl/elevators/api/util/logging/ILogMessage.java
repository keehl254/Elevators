package me.keehl.elevators.api.util.logging;

import java.util.logging.Level;

public interface ILogMessage {

    String getMessage();

    Throwable getThrowable();

    Level getLevel();

    void setMessage(String message);
}