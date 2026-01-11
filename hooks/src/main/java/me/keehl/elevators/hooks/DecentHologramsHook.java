package me.keehl.elevators.hooks;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.hooks.HologramHook;
import me.keehl.elevators.api.models.hooks.IElevatorHologram;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;

import java.util.*;

public class DecentHologramsHook implements HologramHook {

    @Override
    public IElevatorHologram createHologram(UUID uuid, IElevator elevator, String... lines) {
        return new DecentHologramWrapper(uuid, elevator);
    }

    @Override
    public void onInit() {
    }

    public static class DecentHologramWrapper implements IElevatorHologram {

        private final Hologram hologram;

        public DecentHologramWrapper(UUID uuid, IElevator elevator, String... lines) {
            this.hologram = DHAPI.createHologram(uuid.toString(), elevator.getLocation().clone());
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
