package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.models.hooks.HologramHook;
import com.lkeehl.elevators.models.hooks.WrappedHologram;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
import org.bukkit.Location;

import java.util.*;

public class FancyHologramsHook extends HologramHook<FancyHologramsHook.FancyHologramWrapper> {

    private final Map<String, FancyHologramsHook.FancyHologramWrapper> holograms = new HashMap<>();

    @Override
    public FancyHologramWrapper createHologram(Location location, double raise, String... lines) {

        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while(holograms.containsKey(uuid.toString()));

        location = location.clone().add(0, raise, 0);

        FancyHologramsHook.FancyHologramWrapper hologram = new FancyHologramsHook.FancyHologramWrapper(uuid.toString(), location, lines);

        holograms.put(uuid.toString(), hologram);

        return hologram;
    }

    @Override
    public void clearAll() {
        this.holograms.values().forEach(FancyHologramsHook.FancyHologramWrapper::delete);
    }

    public class FancyHologramWrapper extends WrappedHologram {

        private final Hologram hologram;
        public FancyHologramWrapper(String name, Location location, String... lines) {
            super(location);

            HologramData hologramData = new HologramData(name);
            hologramData.setText(Arrays.asList(lines));

            this.hologram = FancyHologramsPlugin.get().getHologramManager().create(hologramData);
        }

        @Override
        public void addLine(String text) {
            HologramData data = this.hologram.getData();
            List<String> hologramText = new ArrayList<>(data.getText());
            hologramText.add(text);
            data.setText(hologramText);

            this.hologram.updateHologram();
        }

        @Override
        public void clearLines() {
            HologramData data = this.hologram.getData();
            data.setText(new ArrayList<>());

            this.hologram.updateHologram();
        }

        @Override
        public double getHeight() {
            return this.hologram.getData().getScale().y; // I know this isn't really how this works >_>
        }

        @Override
        public void teleportTo(Location location) {
            this.hologram.getData().setLocation(location);
            this.hologram.updateHologram();
        }

        @Override
        public void delete() {
            this.hologram.deleteHologram();
            FancyHologramsHook.this.holograms.remove(this.hologram.getData().getName());
        }
    }

}
