package me.keehl.elevators.api.util.logging;

import java.util.List;

public interface ILogReleaseData {

    long getElapsedTime();

    List<ILogMessage> getLogs();
}