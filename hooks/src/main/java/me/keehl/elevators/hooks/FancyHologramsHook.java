package me.keehl.elevators.hooks;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.hooks.HologramHook;
import me.keehl.elevators.api.models.hooks.IElevatorHologram;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Display;

import java.util.*;

public class FancyHologramsHook implements HologramHook {

    @Override
    public IElevatorHologram createHologram(UUID uuid, IElevator elevator, String... lines) {
        return new FancyHologramWrapper(uuid, elevator, lines);
    }

    @Override
    public void onInit() {
    }

    public static class FancyHologramWrapper implements IElevatorHologram {

        private final Hologram hologram;
        public FancyHologramWrapper(UUID uuid, IElevator elevator, String... lines) {
            TextHologramData textData = new TextHologramData(uuid.toString(), elevator.getLocation().clone());
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
        }
    }

}
