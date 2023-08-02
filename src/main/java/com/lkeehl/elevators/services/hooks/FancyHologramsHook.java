package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.models.hooks.HologramHook;
import com.lkeehl.elevators.models.hooks.WrappedHologram;
import org.bukkit.Location;

public class FancyHologramsHook extends HologramHook<FancyHologramsHook.FancyHologramWrapper> {

    @Override
    public FancyHologramWrapper createHologram(Location location, double raise, String... lines) {
        return null;
    }

    @Override
    public void clearAll() {

    }

    public static class FancyHologramWrapper extends WrappedHologram {

        public FancyHologramWrapper(Location location) {
            super(location);

        }

        @Override
        public void addLine(String text) {

        }

        @Override
        public void clearLines() {

        }

        @Override
        public double getHeight() {
            return 0;
        }

        @Override
        public void teleportTo(Location location) {

        }

        @Override
        public void delete() {

        }
    }

}
