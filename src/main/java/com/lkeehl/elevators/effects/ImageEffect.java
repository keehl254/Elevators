package com.lkeehl.elevators.effects;

import com.lkeehl.elevators.models.ElevatorEffect;
import com.lkeehl.elevators.models.ElevatorSearchResult;
import com.lkeehl.elevators.models.ElevatorType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ImageEffect extends ElevatorEffect {

    private static String[][] patternHex;

    private final float duration;

    private final boolean useHolo;

    public ImageEffect(InputStream fileInputStream, int scale, float duration, boolean useHolo, String hexBackgroundColor) {
        scale = Math.max(0, Math.min(100, scale));

        this.duration = duration;
        this.useHolo = useHolo;

        BufferedImage in = ImageIO.read(fileInputStream);



    }

    @Override
    public void playEffect(ElevatorSearchResult teleportResult, ElevatorType elevatorType, byte direction) {

    }
}
