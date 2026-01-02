package me.keehl.elevators.hooks;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.HologramHook;
import me.keehl.elevators.models.hooks.WrappedHologram;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;

import java.util.*;

public class DecentHologramsHook extends HologramHook {

    @Override
    public WrappedHologram createHologram(Elevator elevator, String... lines) {
        return new DecentHologramWrapper(elevator);
    }

    @Override
    public void onInit() {
    }

    public static class DecentHologramWrapper extends WrappedHologram {

        private final Hologram hologram;

        public DecentHologramWrapper(Elevator elevator, String... lines) {
            super(elevator);

            this.hologram = DHAPI.createHologram(this.getUUID(), elevator.getLocation().clone());
            this.hologram.setDownOrigin(true);

            Arrays.stream(lines).forEach(i -> DHAPI.addHologramLine(this.hologram, i));

            this.hologram.realignLines();
        }

        @Override
        public void addLine(String text) {
            DHAPI.addHologramLine(this.hologram, text);
        }

        @Override
        public void setLines(List<String> text) {
            DHAPI.setHologramLines(this.hologram, text);
        }

        @Override
        public double getHeight() {
            return this.hologram.getPage(0).getHeight();
        }

        @Override
        public void teleportTo(Location location) {
            DHAPI.moveHologram(this.hologram, location);
        }

        @Override
        public void onDelete() {
            this.hologram.delete();
        }
    }

}
