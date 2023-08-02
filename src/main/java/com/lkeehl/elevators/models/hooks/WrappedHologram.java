package com.lkeehl.elevators.models.hooks;

import org.bukkit.Location;

public abstract class WrappedHologram {

    public WrappedHologram(Location location) {}

    public abstract void addLine(String text);

    public abstract void clearLines();

    public abstract double getHeight();

    public abstract void teleportTo(Location location);

    public abstract void delete();

}
