package com.lkeehl.elevators.services.hooks;

import com.lkeehl.elevators.models.hooks.HologramHook;
import com.lkeehl.elevators.models.hooks.WrappedHologram;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramType;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;

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

            DisplayHologramData displayData = DisplayHologramData.getDefault(location);
            displayData.setBillboard(Display.Billboard.FIXED);

            TextHologramData textData = TextHologramData.getDefault(name);
            Arrays.stream(lines).forEach(textData::addLine);

            HologramData hologramData = new HologramData(name, displayData, HologramType.TEXT, textData);
            this.hologram = FancyHologramsPlugin.get().getHologramManager().create(hologramData);
        }

        @Override
        public void addLine(String text) {
            TextHologramData data = (TextHologramData) this.hologram.getData().getTypeData();
            List<String> hologramText = new ArrayList<>(data.getText());
            hologramText.add(text);
            data.setText(hologramText);

            this.hologram.updateHologram();
            hologram.refreshHologram(Bukkit.getOnlinePlayers());
        }

        @Override
        public void clearLines() {
            TextHologramData data = (TextHologramData) this.hologram.getData().getTypeData();
            data.setText(new ArrayList<>());

            this.hologram.updateHologram();
            hologram.refreshHologram(Bukkit.getOnlinePlayers());
        }

        @Override
        public double getHeight() {
            return this.hologram.getData().getDisplayData().getScale().y(); //.getScale().y; // I know this isn't really how this works >_>
        }

        @Override
        public void teleportTo(Location location) {
            this.hologram.getData().getDisplayData().setLocation(location);
            this.hologram.updateHologram();
            hologram.refreshHologram(Bukkit.getOnlinePlayers());
        }

        @Override
        public void delete() {
            this.hologram.deleteHologram();
            FancyHologramsHook.this.holograms.remove(this.hologram.getData().getName());
        }
    }

}
