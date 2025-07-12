package me.keehl.elevators.services.configs.versions.configv5_2_0;

import me.keehl.elevators.util.config.Comments;
import me.keehl.elevators.util.config.Config;

public class ConfigEffect implements Config {

    @Comments("The image file that the effect will try to recreate.")
    public String file = "Creeper.png";

    @Comments("Scales down the effect to be a percentage of original images width and height.")
    public int scale = 1;

    @Comments("Controls how long the effect will be present before disappearing.")
    public float duration = 1.0F;

    @Comments("Elevators can use particles to create the effect (heavy on potato computers), or can hook into Holograms.")
    public boolean useHolo = true;

    @Comments("Any color of this hex found in the image file will be made transparent.")
    public String background = "#FFFFFF";

}
