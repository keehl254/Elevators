package com.lkeehl.elevators.effects;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorEffect;
import com.lkeehl.elevators.models.ElevatorEventData;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArrowEffect extends ElevatorEffect {

    public ArrowEffect() {
        super("ARROW", ItemStackHelper.createItem("Arrow", Material.TIPPED_ARROW, 1));
    }

    @Override
    public void playEffect(ElevatorEventData teleportResult, Elevator elevator) {
        byte direction = teleportResult.getDirection();

        Color particleColor = this.getParticleColor(elevator);

        Location locClone = getEffectLocation(elevator).getBlock().getLocation();
        locClone.add(0.5, direction == 1 ? 1 : 3, 0.5);

        List<Location> spawnPositions = new ArrayList<>();

        for(int i=0;i<20;i++)
            spawnPositions.add(locClone.add(0, direction * 0.1, 0).clone());

        for(int i=5;i>=0;i--) {
            for(int z=0;z<2;z++) {
                Location clone = locClone.clone();
                double y = direction == 1 ? -i * 0.1 : i * 0.1;
                if(z == 0) {
                    clone.add(-i*0.1, y, 0);
                    spawnPositions.add(clone.clone());
                    clone.add(i*0.2, 0, 0);
                }else {
                    clone.add(0, y, -i*0.1);
                    spawnPositions.add(clone.clone());
                    clone.add(0, 0, i*0.2);
                }
                spawnPositions.add(clone.clone());

            }
        }

        for(int i=0;i<10;i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Elevators.getInstance(), () -> {
                for(Location location1 : spawnPositions)
                    Objects.requireNonNull(location1.getWorld()).spawnParticle(Particle.DUST, location1, 1, 0, 0, 0, 1, new Particle.DustOptions(particleColor, 1));
            }, i*2);
        }

    }
}
