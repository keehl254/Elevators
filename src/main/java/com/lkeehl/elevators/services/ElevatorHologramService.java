package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.hooks.WrappedHologram;
import com.lkeehl.elevators.services.configs.versions.configv5.ConfigRoot;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Chunk;
import org.bukkit.Tag;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Collection;

public class ElevatorHologramService {

    private static boolean initialized = false;


    private static WrappedTask task;
    private static int currentIndex = 0;

    public static void init() {
        if(ElevatorHologramService.initialized)
            return;

        ElevatorConfigService.addConfigCallback(ElevatorHologramService::onConfigReload);

        task = Elevators.getFoliaLib().getScheduler().runTimer(() -> {
            WrappedHologram[] holograms = ElevatorHookService.getHologramHook().getHolograms().toArray(new WrappedHologram[]{});
            if(holograms.length == 0)
                return;

            // Try to update 10 holos at a time.
            int attempts = 0;
            int startIndex = currentIndex;
            while(attempts++ < 10) {
                currentIndex = currentIndex % holograms.length;
                WrappedHologram hologram = holograms[currentIndex];
                if(!hologram.getElevatorLocation().getChunk().isLoaded())
                    continue;

                hologram.update();
                currentIndex++;

                // We've come full circle looking for 10 holos to update.
                if(startIndex == currentIndex)
                    return;
            }

        }, 5,5);

        ElevatorHologramService.initialized = true;
    }

    public static void onDisable() {
        clearAll();
        task.cancel();
    }

    private static void onConfigReload(ConfigRoot config) {

        if(!canUseHolograms())
            return;

        //for(ElevatorType elevatorType : ElevatorTypeService.getExistingElevatorTypes())
        //    elevatorType.updateAllHolograms(true);
    }

    public static WrappedHologram getElevatorHologramIfExists(Elevator elevator) {

        ShulkerBox shulkerBox = elevator.getShulkerBox();
        if(shulkerBox.hasMetadata("elevator-holo-uuid")) {
            String hologramUUID = shulkerBox.getMetadata("elevator-holo-uuid").getFirst().asString();
            WrappedHologram hologram = ElevatorHookService.getHologramHook().getHologram(hologramUUID);
            if(hologram != null)
                return hologram;

            // It had the metadata, but the hologram could not be found.
            shulkerBox.removeMetadata("elevator-holo-uuid", Elevators.getInstance());
        }
        return null;
    }

    public static WrappedHologram getElevatorHologram(Elevator elevator) {

        if(!canUseHolograms())
            return null;

        WrappedHologram hologram = getElevatorHologramIfExists(elevator);
        if(hologram != null)
            return hologram;

        ShulkerBox shulkerBox = elevator.getShulkerBox();

        hologram = ElevatorHookService.getHologramHook().createHologram(elevator, ElevatorHologramService::deleteHologram);
        shulkerBox.setMetadata("elevator-holo-uuid", new FixedMetadataValue(Elevators.getInstance(), hologram.getUUID()));

        updateElevatorHologram(elevator);
        return hologram;
    }

    public static void deleteHologram(Elevator elevator) {

        WrappedHologram hologram = getElevatorHologram(elevator);
        if(hologram == null)
            return;

        hologram.delete();
    }

    public static void deleteHologramsInChunk(Chunk ignoredChunk) {

        /* We used to delete holograms when chunks were unloaded; however, there was an issue
           where deleting the hologram would reload the chunk... Which would load the elevator
           ... which would recreate the hologram... and then the chunk unloads... repeat.

           At this point in time, all of our hooked hologram plugins' API do not store
           plugin-created holograms without specifying to do so. Let's leave the loading
           and unloading to those plugins instead.

           In the worst case, we can create our own HologramWrapper for 1.19.4+ and have it use
           Text DisplayEntities. It would not be hard, but I would prefer not to be liable for
           stuck holograms if it ever borked for some reason.
         */

    }

    public static void updateHologramsInChunk(Chunk chunk) {
        if(!canUseHolograms())
            return;

        Collection<BlockState> tileEntities;
        if (ElevatorHookService.isServerRunningPaper())
            tileEntities = chunk.getTileEntities(block -> Tag.SHULKER_BOXES.isTagged(block.getType()), false);
        else
            tileEntities = List.of(chunk.getTileEntities());

        for (BlockState state : tileEntities) {
            if (!(state instanceof ShulkerBox box))
                continue;

            ElevatorType elevatorType = ElevatorHelper.getElevatorType(box);
            if(elevatorType == null)
                continue;

            ElevatorHologramService.updateElevatorHologram(new Elevator(box, elevatorType));
        }
    }

    public static void updateElevatorHologram(Elevator elevator) {

        if(!canUseHolograms())
            return;

        if(elevator == null)
            return;

        List<String> hologramLines = elevator.getElevatorType().getHolographicLines().stream().map(i -> MessageHelper.formatPlaceholders(null, i)).toList();

        WrappedHologram hologram = hologramLines.isEmpty() ? getElevatorHologramIfExists(elevator) : getElevatorHologram(elevator);
        if(hologram == null) // We delete holograms that are empty. No need to "create" the hologram just to delete it.
            return;

        if(hologramLines.isEmpty()) {
            hologram.delete();
            return;
        }

        hologram.setLines(hologramLines);
        hologram.teleportTo(elevator.getLocation().clone().add(0.5, 1.5 + (hologram.getHeight()) / 2, 0.5));
    }

    public static void updateHologramsOfElevatorType(ElevatorType elevatorType) {
        List<? extends WrappedHologram> holograms = ElevatorHookService.getHologramHook().getHolograms().stream().filter(i -> i.getElevatorType().equals(elevatorType)).toList();
        for(WrappedHologram hologram : holograms)
            hologram.update();
    }

    private static void deleteHologram(WrappedHologram hologram) {
        Elevator elevator = hologram.getElevator();
        if(elevator != null && elevator.getShulkerBox() != null)
            elevator.getShulkerBox().removeMetadata("elevator-holo-uuid", Elevators.getInstance());
    }

    public static void clearAll() {
        ElevatorHookService.getHologramHook().clearAll();
    }

    public static boolean canUseHolograms() {
        return ElevatorHookService.getHologramHook() != null;
    }

}
