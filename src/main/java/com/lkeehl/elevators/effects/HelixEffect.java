package com.lkeehl.elevators.effects;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.ElevatorEffect;
import com.lkeehl.elevators.models.ElevatorEventData;
import com.lkeehl.elevators.util.ExecutionMode;
import org.bukkit.*;

import java.util.Objects;

public class HelixEffect extends ElevatorEffect {

    public HelixEffect() {
        super("HELIX", ItemStackHelper.createItem("Helix", Material.HOPPER, 1));
    }

    @Override
    public void playEffect(ElevatorEventData teleportResult, ExecutionMode executionMode) {
        Location locClone = this.getEffectLocation(teleportResult, executionMode).getBlock().getLocation();
        Color particleColor = this.getParticleColor(teleportResult);

        World world = locClone.getWorld();

        locClone.add(0, 1, 0);
        for (int i = 0; i < 2000; i += 50) {
            float x = (float) ((Math.sin(i / 7.0D)) * 0.5);
            float z = (float) ((Math.cos(i / 7.0D)) * 0.5);
            float y = (float) ((Math.cos(i / 7.0D) * 0.6D + 0.5D) * 0.2);

            int tempI = i;

            Bukkit.getScheduler().scheduleSyncDelayedTask(Elevators.getInstance(), () -> {
                    Objects.requireNonNull(world).spawnParticle(Particle.DUST, locClone.add(x, y, z), 1, 0, 0, 0, 1, new Particle.DustOptions(particleColor, ((2000-tempI)*2F)/2000.0F));
            }, i  / 50L);
        }
    }
}
