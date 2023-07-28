package com.lkeehl.elevators.effects;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.models.ElevatorEffect;
import com.lkeehl.elevators.models.ElevatorSearchResult;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.*;

import java.util.Objects;

public class HelixEffect extends ElevatorEffect {

    public HelixEffect() {
        super("HELIX");
    }

    @Override
    public void playEffect(ElevatorSearchResult teleportResult, ElevatorType elevatorType, byte direction) {
        Location locClone = this.getEffectLocation(teleportResult).getBlock().getLocation();
        Color particleColor = this.getParticleColor(teleportResult);

        World world = locClone.getWorld();

        //location.add(0.5, 0, 0.5);
        for (int i = 0; i < 2000; i += 50) {
            float x = (float) ((Math.sin(i / 7.0D)) * 0.5);
            float z = (float) ((Math.cos(i / 7.0D)) * 0.5);
            float y = (float) ((Math.cos(i / 7.0D) * 0.6D + 0.5D) * 0.2);

            int tempI = i;

            Bukkit.getScheduler().scheduleSyncDelayedTask(Elevators.getInstance(), () -> {
                    Objects.requireNonNull(world).spawnParticle(Particle.REDSTONE, locClone.add(x, y, z), 1, 0, 0, 0, 1, new Particle.DustOptions(particleColor, ((2000-tempI)*2F)/2000.0F));
            }, i  / 50L);
        }
    }
}
