package com.lkeehl.elevators.services.configs;

import org.spongepowered.configurate.objectmapping.meta.Comment;

public class ConfigEffect {

    @Comment("The image file that the effect will try to recreate.")
    public String file = "Creeper.png";

    @Comment("Scales down the effect to be a percentage of original images width and height.")
    public int scale = 50;

    @Comment("Controls how long the effect will be present before disappearing.")
    public float duration = 1.0F;

    @Comment("Elevators can use particles to create the effect (heavy on potato computers), or can hook into Holograms.")
    public boolean useHolo = true;

    @Comment("Any color of this hex found in the image file will be made transparent.")
    public String background = "#FFFFFF";

}
