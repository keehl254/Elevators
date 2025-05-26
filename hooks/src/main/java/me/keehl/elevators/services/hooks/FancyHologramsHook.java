package me.keehl.elevators.services.hooks;

import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.hooks.HologramHook;
import me.keehl.elevators.models.hooks.WrappedHologram;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Display;

import java.util.*;
import java.util.function.Consumer;

public class FancyHologramsHook extends HologramHook<FancyHologramsHook.FancyHologramWrapper> {

    private final Map<String, FancyHologramWrapper> holograms = new HashMap<>();

    @Override
    public FancyHologramWrapper createHologram(Elevator elevator, Consumer<WrappedHologram> deleteConsumer, String... lines) {

        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while(this.holograms.containsKey(uuid.toString()));

        FancyHologramWrapper hologram = new FancyHologramWrapper(uuid.toString(), elevator, deleteConsumer, lines);

        this.holograms.put(uuid.toString(), hologram);

        return hologram;
    }

    @Override
    public void clearAll() {
        new ArrayList<>(this.holograms.values()).forEach(FancyHologramWrapper::delete);
    }

    @Override
    public Collection<FancyHologramWrapper> getHolograms() {
        return this.holograms.values();
    }

    @Override
    public FancyHologramWrapper getHologram(String uuid) {
        return this.holograms.get(uuid);
    }

    public class FancyHologramWrapper extends WrappedHologram {

        private final Hologram hologram;
        public FancyHologramWrapper(String uuid, Elevator elevator, Consumer<WrappedHologram> deleteConsumer, String... lines) {
            super(uuid, elevator, deleteConsumer);

            TextHologramData textData = new TextHologramData(uuid, elevator.getLocation().clone());
            Arrays.stream(lines).forEach(textData::addLine);

            textData.setBillboard(Display.Billboard.CENTER);
            textData.setPersistent(false);

            this.hologram = FancyHologramsPlugin.get().getHologramManager().create(textData);
            FancyHologramsPlugin.get().getHologramManager().addHologram(this.hologram);

        }

        @Override
        public void addLine(String text) {
            TextHologramData data = (TextHologramData) this.hologram.getData();
            List<String> hologramText = new ArrayList<>(data.getText());
            hologramText.add(text);
            data.setText(hologramText);

            this.hologram.queueUpdate();
        }

        @Override
        public void setLines(List<String> text) {
            TextHologramData data = (TextHologramData) this.hologram.getData();
            data.setText(text);

            this.hologram.queueUpdate();
        }

        @Override
        public void clearLines() {
            TextHologramData data = (TextHologramData) this.hologram.getData();
            data.setText(new ArrayList<>());

            this.hologram.queueUpdate();
        }

        @Override
        public double getHeight() {
            return ((TextHologramData)this.hologram.getData()).getScale().y();
        }

        @Override
        public void teleportTo(Location location) {
            this.hologram.getData().setLocation(location);
            this.hologram.queueUpdate();
        }

        @Override
        public void onDelete() {
            FancyHologramsPlugin.get().getHologramManager().removeHologram(this.hologram);
            FancyHologramsHook.this.holograms.remove(this.getUUID());
        }
    }

}
