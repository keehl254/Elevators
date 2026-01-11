package me.keehl.elevators.api.models.hooks;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import org.bukkit.Location;

import java.util.List;

public interface IElevatorHologram {

    void addLine(String text);

    void setLines(List<String> text);

    double getHeight();

    void teleportTo(Location location);

    void onDelete();

}
