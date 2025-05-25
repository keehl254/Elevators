package me.keehl.elevators.effects;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorEffect;
import me.keehl.elevators.models.ElevatorEventData;
import org.bukkit.*;

import java.util.Objects;

public class HelixEffect extends ElevatorEffect {

    public HelixEffect() {
        super("HELIX", ItemStackHelper.createItem("Helix", Material.HOPPER, 1));
    }

    @SuppressWarnings("CodeBlock2Expr")
    @Override
    public void playEffect(ElevatorEventData teleportResult, Elevator elevator) {
        Location locClone = this.getEffectLocation(elevator).getBlock().getLocation();
        Color particleColor = this.getParticleColor(elevator);

        World world = locClone.getWorld();

        locClone.add(0, 1, 0);
        for (int i = 0; i < 2000; i += 50) {
            float x = (float) ((Math.sin(i / 7.0D)) * 0.5);
            float z = (float) ((Math.cos(i / 7.0D)) * 0.5);
            float y = (float) ((Math.cos(i / 7.0D) * 0.6D + 0.5D) * 0.2);

            int tempI = i;

            Elevators.getFoliaLib().getScheduler().runAtLocationLater(elevator.getLocation(), task -> {
                    Objects.requireNonNull(world).spawnParticle(Particle.REDSTONE, locClone.add(x, y, z), 1, 0, 0, 0, 1, new Particle.DustOptions(particleColor, ((2000-tempI)*2F)/2000.0F));
            }, i  / 50L);
        }
    }
}
