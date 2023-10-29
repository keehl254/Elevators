package com.lkeehl.elevators.effects;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ColorHelper;
import com.lkeehl.elevators.models.ElevatorEffect;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.models.hooks.WrappedHologram;
import com.lkeehl.elevators.services.HookService;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.Color;
import org.bukkit.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageEffect extends ElevatorEffect {

    private final int[][] rgbPattern;

    private final float duration;

    private final boolean useHolo;

    private final int height;

    public ImageEffect(String imageEffectKey, File file, int scale, float duration, boolean useHolo, String hexBackgroundColor) {
        super(imageEffectKey);

        scale = Math.max(0, Math.min(100, scale));
        int backgroundRGB = ColorHelper.getRGBFromHex(hexBackgroundColor);

        this.duration = duration;
        this.useHolo = useHolo && HookService.getHologramHook() != null;

        int height = 0;
        int[][] rgbPattern = new int[][]{};

        try {
            BufferedImage image = ImageIO.read(file);

            Dimension scaledDimension = new Dimension((scale * image.getWidth()) / 100, (scale * image.getHeight()) / 100);
            if (scaledDimension.getWidth() < scaledDimension.getHeight() && scaledDimension.getWidth() < 16)
                scaledDimension.setSize(16, (scaledDimension.getHeight() * 16) / scaledDimension.getWidth());
            else if (scaledDimension.getHeight() < 16)
                scaledDimension.setSize((scaledDimension.getWidth() * 16) / scaledDimension.getHeight(), 16);

            image = new BufferedImage((int) scaledDimension.getWidth(), (int) scaledDimension.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Image toolKitImage = image.getScaledInstance(image.getWidth(), image.getHeight(), Image.SCALE_SMOOTH);
            Graphics g = image.getGraphics();
            g.drawImage(toolKitImage, 0, 0, null);
            g.dispose();

            rgbPattern = new int[image.getWidth()][image.getHeight()];
            for (int x = 0; x < rgbPattern.length; x++) {
                for (int y = 0; y < rgbPattern[x].length; y++) {
                    int rgb = image.getRGB(x, y);
                    rgbPattern[x][y] = (rgb == backgroundRGB || (rgb >> 24) == 0) ? 0 : rgb;
                }
            }

            height = image.getHeight();
        }catch (IOException e){
            e.printStackTrace();
            Elevators.getElevatorsLogger().warning("Error loading image for effect \"" + this.getEffectKey() + "\". Effect disabled.");
        }
        this.height = height;
        this.rgbPattern = rgbPattern;
    }

    private void playHoloEffect(Location location) {
        try {
            String[] lines = new String[this.height];
            for(int[] rgbPattern : rgbPattern) {
                for (int y = 0; y < rgbPattern.length; y++)
                    lines[y] = (lines[y] != null ? lines[y] : "") + (rgbPattern[y] == 0 ? ChatColor.BOLD + " " + ChatColor.RESET + " " : ColorHelper.getChatStringFromColor(rgbPattern[y]) + "█");
            }

            WrappedHologram hologram = HookService.getHologramHook().createHologram(location, 0.0, lines);
            if (hologram != null)
                Bukkit.getScheduler().scheduleSyncDelayedTask(Elevators.getInstance(), hologram::delete, (long) (duration * 20));
        } catch (Exception e) {
            Elevators.getElevatorsLogger().warning("Effect \"" + this.getEffectKey() + "\" is too wide to use holographic displays. Max width is 150");
        }
    }

    private void playParticleEffect(Location location) {
        if(location.getWorld() == null)
            return;

        double size = this.rgbPattern.length * 0.2;
        double offset = ((size * 4.5) / 2.0) * 0.2;
        location.add(0, this.height * 0.2, 0);
        for (int time = 0; time < duration * 20; time++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Elevators.getInstance(), () -> {
                for (int side = 0; side < 4; side++) {
                    for (int x = 0; x < this.rgbPattern.length; x++) {
                        for (int y = 0; y < this.rgbPattern[x].length; y++) {
                            int tempX = (this.rgbPattern.length - 1) - x;
                            Location locClone = location.clone();
                            if (side == 0)
                                locClone.subtract((offset - 0.5) + (tempX * 0.2), (y * 0.2), offset);
                            else if (side == 1)
                                locClone.add((x * 0.2) - (offset - 0.5), -(y * 0.2), (offset + 1.0));
                            else if (side == 2)
                                locClone.subtract(offset, (y * 0.2), (offset - 0.5) - (x * 0.2));
                            else
                                locClone.add((offset + 1.0), -(y * 0.2), (tempX * 0.2) - (offset - 0.5));

                            location.getWorld().spawnParticle(Particle.REDSTONE, locClone, 1, 0, 0, 0, 1, new Particle.DustOptions(Color.fromRGB(this.rgbPattern[x][y]), 1));
                        }
                    }
                }
            }, time);
        }
    }

    @Override
    public void playEffect(ElevatorEventData teleportResult, ExecutionMode executionMode) {
        if(this.height <= 0)
            return;

        Location location = this.getEffectLocation(teleportResult, executionMode).add(0.5, 0.5, 0.5);

        if(this.useHolo)
            this.playHoloEffect(location);
        else
            this.playParticleEffect(location);

    }
}
