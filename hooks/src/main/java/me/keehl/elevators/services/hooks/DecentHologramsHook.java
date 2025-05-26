package me.keehl.elevators.services.hooks;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.HologramHook;
import me.keehl.elevators.models.hooks.WrappedHologram;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import org.bukkit.Location;

import java.util.*;
import java.util.function.Consumer;

public class DecentHologramsHook extends HologramHook<DecentHologramsHook.DecentHologramWrapper> {

    private final Map<String, DecentHologramWrapper> holograms = new HashMap<>();

    @Override
    public DecentHologramWrapper createHologram(Elevator elevator, Consumer<WrappedHologram> deleteConsumer, String... lines) {

        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while(this.holograms.containsKey(uuid.toString()));

        DecentHologramWrapper hologram = new DecentHologramWrapper(uuid.toString(), elevator, deleteConsumer);
        Arrays.stream(lines).forEach(hologram::addLine);

        this.holograms.put(uuid.toString(), hologram);

        return hologram;
    }

    @Override
    public void clearAll() {
        new ArrayList<>(this.holograms.values()).forEach(DecentHologramWrapper::delete);
    }

    @Override
    public Collection<DecentHologramWrapper> getHolograms() {
        return this.holograms.values();
    }

    @Override
    public DecentHologramWrapper getHologram(String uuid) {
        return this.holograms.get(uuid);
    }

    public class DecentHologramWrapper extends WrappedHologram {

        private final Hologram hologram;

        public DecentHologramWrapper(String uuid, Elevator elevator, Consumer<WrappedHologram> deleteConsumer) {
            super(uuid, elevator, deleteConsumer);

            this.hologram = DHAPI.createHologram(uuid, elevator.getLocation().clone());
            this.hologram.setDownOrigin(true);
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
        public void clearLines() {
            HologramPage page = this.hologram.getPage(0);
            for(int i = page.getLines().size()-1; i >=0; i--)
                page.removeLine(i);
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
            DecentHologramsHook.this.holograms.remove(this.getUUID());
        }
    }

}
