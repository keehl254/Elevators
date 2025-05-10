package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.helpers.MessageHelper;
import com.lkeehl.elevators.models.Elevator;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.models.hooks.WrappedHologram;
import com.lkeehl.elevators.services.configs.ConfigRoot;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElevatorHologramService {

    private static boolean initialized = false;

    // So glad that Java passes objects by reference or the memory would cry.
    public static List<WrappedHologram> holograms = new ArrayList<>();

    private static final Map<Location, WrappedHologram> elevatorHolograms = new HashMap<>();
    private static final Map<ElevatorType, List<WrappedHologram>> elevatorTypeHolograms = new HashMap<>();

    private static final List<Long> ignoreLoadChunks = new ArrayList<>();

    private static WrappedTask task;
    private static int currentIndex = 0;

    public static void init() {
        if(ElevatorHologramService.initialized)
            return;

        ElevatorConfigService.addConfigCallback(ElevatorHologramService::onConfigReload);

        task = Elevators.getFoliaLib().getScheduler().runTimer(() -> {
            if(holograms.isEmpty())
                return;

            currentIndex = currentIndex % holograms.size();
            holograms.get(currentIndex).update();
            currentIndex++;

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

        for(ElevatorType elevatorType : ElevatorTypeService.getExistingElevatorTypes())
            elevatorType.updateAllHolograms(true);

        for(Location location : new ArrayList<>(elevatorHolograms.keySet())) {

            WrappedHologram hologram = elevatorHolograms.get(location);
            if(!location.getChunk().isLoaded())
                hologram.delete();

        }
    }

    public static WrappedHologram getElevatorHologram(Elevator elevator) {

        if(!canUseHolograms())
            return null;

        if(elevatorHolograms.containsKey(elevator.getLocation()))
            return elevatorHolograms.get(elevator.getLocation());

        WrappedHologram hologram = ElevatorHookService.getHologramHook().createHologram(elevator.getLocation(), ElevatorHologramService::deleteHologram);
        holograms.add(hologram);
        elevatorHolograms.put(elevator.getLocation(), hologram);

        if(!elevatorTypeHolograms.containsKey(elevator.getElevatorType()))
            elevatorTypeHolograms.put(elevator.getElevatorType(), new ArrayList<>());

        elevatorTypeHolograms.get(elevator.getElevatorType()).add(hologram);

        updateElevatorHologram(elevator);
        return hologram;
    }

    public static void deleteHologram(Elevator elevator) {

        if(!elevatorHolograms.containsKey(elevator.getLocation()))
            return;

        WrappedHologram hologram = getElevatorHologram(elevator);
        if(hologram == null)
            return;

        hologram.delete();
    }

    public static void loadHologramsInChunk(Chunk chunk) {
        long chunkKey = (long)chunk.getX() & 4294967295L | ((long)chunk.getZ() & 4294967295L) << 32;

         if(ignoreLoadChunks.contains(chunkKey)) {
             ignoreLoadChunks.remove(chunkKey);
             return;
         }

        for (BlockState state : chunk.getTileEntities()) {
            if(!(state instanceof ShulkerBox box))
                continue;
            ElevatorType elevatorType = ElevatorHelper.getElevatorType(box);
            if(elevatorType == null)
                continue;
            ElevatorHologramService.updateElevatorHologram(new Elevator(box, elevatorType));
        }
    }

    public static void deleteHologramsInChunk(Chunk chunk) {

        /* Temporarily disabled while working out how to delete holograms without also reloading the chunk.

        Elevators.getElevatorsLogger().warning("Chunk unloaded. Starting to remove holograms in chunk.");
        long chunkKey = (long)chunk.getX() & 4294967295L | ((long)chunk.getZ() & 4294967295L) << 32;

        List<WrappedHologram> toDelete = new ArrayList<>();
        for(Location location : elevatorHolograms.keySet()) {

            int chunkX = location.getBlockX() >> 4;
            int chunkY = location.getBlockZ() >> 4;
            long myChunkKey = (long)chunkX & 4294967295L | ((long)chunkY & 4294967295L) << 32;

            if(myChunkKey == chunkKey)
                toDelete.add(elevatorHolograms.get(location));

        }
        ignoreLoadChunks.add(chunkKey);
        toDelete.forEach(WrappedHologram::delete);
         */
    }

    public static void updateElevatorHologram(Elevator elevator) {

        if(elevator == null)
            return;

        if(!canUseHolograms())
            return;

        List<String> hologramLines = elevator.getElevatorType().getHolographicLines().stream().map(i -> MessageHelper.formatPlaceholders(null, i)).toList();
        if(hologramLines.isEmpty() && !elevatorHolograms.containsKey(elevator.getLocation())) // We delete holograms that are empty. No need to "create" the hologram just to delete it.
            return;

        WrappedHologram hologram = getElevatorHologram(elevator);
        if(hologram == null)
            return;

        if(hologramLines.isEmpty()) {
            hologram.delete();
            return;
        }

        hologram.setLines(hologramLines);
        hologram.teleportTo(elevator.getLocation().clone().add(0.5, 1.5 + (hologram.getHeight()) / 2, 0.5));

    }

    public static void updateHologramsOfElevatorType(ElevatorType elevatorType) {
        for(WrappedHologram hologram : new ArrayList<>(elevatorTypeHolograms.getOrDefault(elevatorType, new ArrayList<>()))) // Roundabout, but avoids co-modification
            hologram.update();
    }

    private static void deleteHologram(WrappedHologram hologram) {
        elevatorHolograms.remove(hologram.getElevatorLocation());
        for(ElevatorType elevatorType : elevatorTypeHolograms.keySet())
            elevatorTypeHolograms.get(elevatorType).remove(hologram);
    }

    public static void clearAll() {
        holograms.forEach(WrappedHologram::delete);
        holograms.clear();
        elevatorHolograms.clear();
        elevatorTypeHolograms.clear();
    }

    public static boolean canUseHolograms() {
        return ElevatorHookService.getHologramHook() != null;
    }

}
