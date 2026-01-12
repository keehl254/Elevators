package me.keehl.elevators.effects;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.hooks.IWrappedHologram;
import me.keehl.elevators.helpers.ColorHelper;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.ResourceHelper;
import me.keehl.elevators.helpers.VersionHelper;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.ElevatorEffect;
import me.keehl.elevators.api.models.IElevatorEventData;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

public class ImageEffect extends ElevatorEffect {

    private final int[][] rgbPattern;

    private final float duration;

    private final boolean useHolo;

    private final int height;

    public ImageEffect(String imageEffectKey, File file, int scale, float duration, boolean useHolo, String hexBackgroundColor) {
        super(imageEffectKey, imageEffectKey.equalsIgnoreCase("creeper") ? ItemStackHelper.createItem("Creeper", Material.CREEPER_HEAD, 1) : null);

        scale = Math.max(0, Math.min(100, scale));
        int backgroundRGB = ColorHelper.getRGBFromHex(hexBackgroundColor);

        this.duration = duration;
        this.useHolo = useHolo && Elevators.getHologramService().canUseHolograms();

        int height = 0;
        int[][] rgbPattern = new int[][]{};

        try {
            BufferedImage image = ImageIO.read(file);

            BufferedImage scaledImage = getBufferedImage(scale, image);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(image, 0, 0, scaledImage.getWidth(), scaledImage.getHeight(), null);
            g2d.dispose();

            image = scaledImage;

            rgbPattern = new int[image.getWidth()][image.getHeight()];
            for (int x = 0; x < rgbPattern.length; x++) {
                for (int y = 0; y < rgbPattern[x].length; y++) {
                    int rgb = image.getRGB(x, y);
                    rgbPattern[x][y] = (rgb == backgroundRGB || (rgb >> 24) == 0) ? 0 : rgb;
                    //rgbPattern[x][y] = rgb == backgroundRGB ? 0 : rgb;
                }
            }

            height = image.getHeight();
        }catch (IOException e){
            ElevatorsAPI.log(Level.WARNING, "Error loading image for effect \"" + this.getEffectKey()+"\". Effect disabled. Please create an issue ticket on my GitHub if one doesn't already exist: https://github.com/keehl254/Elevators/issues. Issue:\n" + ResourceHelper.cleanTrace(e));
        }
        this.height = height;
        this.rgbPattern = rgbPattern;
    }

    private static @NotNull BufferedImage getBufferedImage(int scale, BufferedImage image) {
        Dimension scaledDimension = new Dimension((scale * image.getWidth()) / 100, (scale * image.getHeight()) / 100);
        if (scaledDimension.getWidth() < scaledDimension.getHeight() && scaledDimension.getWidth() < 16)
            scaledDimension.setSize(16, (scaledDimension.getHeight() * 16) / scaledDimension.getWidth());
        else if (scaledDimension.getHeight() < 16)
            scaledDimension.setSize((scaledDimension.getWidth() * 16) / scaledDimension.getHeight(), 16);

        return new BufferedImage((int) scaledDimension.getWidth(), (int) scaledDimension.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    private void playHoloEffect(IElevator elevator) {
        try {
            String[] lines = new String[this.height];
            for(int[] rgbPattern : this.rgbPattern) {
                for (int y = 0; y < rgbPattern.length; y++)
                    lines[y] = (lines[y] != null ? lines[y] : "") + (rgbPattern[y] == 0 ? ChatColor.BOLD + " " + ChatColor.RESET + " " : ColorHelper.getChatStringFromColor(rgbPattern[y]) + "█");
            }

            IWrappedHologram hologram = Elevators.getHologramService().getElevatorHologram(elevator);
            if(hologram == null)
                return;

            hologram.setLines(Arrays.asList(lines));
            hologram.teleportTo(elevator.getLocation().clone().add(0.5, (hologram.getHeight() + 1.5) / 2, 0.5));

            Elevators.getFoliaLib().getScheduler().runAtLocationLater(elevator.getLocation(), () -> Elevators.getHologramService().updateElevatorHologram(elevator), (long) (this.duration * 20));
        } catch (Exception e) {
            ElevatorsAPI.log(Level.WARNING,"Effect \"" + this.getEffectKey() + "\" is too wide to use holographic displays. Max width is 150");
        }
    }

    private void playParticleEffect(Location location) {
        if(location.getWorld() == null)
            return;

        double size = this.rgbPattern.length * 0.2;
        double offset = ((size * 4.5) / 2.0) * 0.2;
        location.add(-0.5, this.height * 0.2, -0.5);
        for (int time = 0; time < this.duration * 20; time++) {
            Elevators.getFoliaLib().getScheduler().runAtLocationLater(location, () -> {
                for (int side = 0; side < 4; side++) {
                    for (int x = 0; x < this.rgbPattern.length; x++) {
                        for (int y = 0; y < this.rgbPattern[x].length; y++) {
                            int colorARGB = this.rgbPattern[x][y];
                            if(colorARGB == 0)
                                continue;

                            int tempX = (this.rgbPattern.length - 1) - x;
                            Location locClone = location.clone();
                            if (side == 0) {
                                locClone.add((tempX * 0.2) - (offset - 0.5), -(y * 0.2), -offset);
                            } else if (side == 1) {
                                locClone.add((x * 0.2) - (offset - 0.5), -(y * 0.2), (offset + 1.0));
                            } else if (side == 2) {
                                locClone.subtract(offset, (y * 0.2), (offset - 0.5) - (x * 0.2));
                            } else {
                                locClone.add((offset + 1.0), -(y * 0.2), (tempX * 0.2) - (offset - 0.5));
                            }

                            location.getWorld().spawnParticle(Particle.REDSTONE, locClone, 1, 0, 0, 0, 1, new Particle.DustOptions(VersionHelper.getDustColor(colorARGB), 1));
                        }
                    }
                }
            }, time);
        }
    }

    @Override
    public void playEffect(IElevatorEventData teleportResult, IElevator elevator) {
        if(this.height <= 0)
            return;

        Location location = this.getEffectLocation(elevator).add(0.5, 0.5, 0.5);
        if(this.useHolo)
            this.playHoloEffect(elevator);
        else
            this.playParticleEffect(location);

    }
}
