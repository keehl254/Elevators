package me.keehl.elevators.services;

import io.github.rvskele.paperlib.PaperLib;
import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.models.hooks.WrappedHologram;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.stream.Collectors;

public class ElevatorHologramService {

    private static boolean initialized = false;

    private static final Map<String, WrappedHologram> holograms = new HashMap<>();

    private static WrappedTask task;
    private static int currentIndex = 0;

    public static void init() {
        if (ElevatorHologramService.initialized)
            return;
        Elevators.pushAndHoldLog();

        task = Elevators.getFoliaLib().getScheduler().runTimer(() -> {
            if (!canUseHolograms())
                return;

            WrappedHologram[] holograms = getHolograms().toArray(new WrappedHologram[]{});
            if (holograms.length == 0)
                return;

            // Try to update 10 holos at a time.
            int attempts = 0;
            int startIndex = currentIndex;
            while (attempts++ < 10) {
                currentIndex = currentIndex % holograms.length;
                WrappedHologram hologram = holograms[currentIndex];
                if (!hologram.getElevatorLocation().getChunk().isLoaded())
                    continue;

                hologram.update();
                currentIndex++;

                // We've come full circle looking for 10 holos to update.
                if (startIndex == currentIndex)
                    return;
            }

        }, 5, 5);

        ElevatorHologramService.initialized = true;
        Elevators.popLog(logData -> Elevators.log("Hologram service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public static void onDisable() {
        clearAll();
        task.cancel();
    }

    public static WrappedHologram getElevatorHologramIfExists(Elevator elevator) {

        ShulkerBox shulkerBox = elevator.getShulkerBox();
        if (shulkerBox.hasMetadata("elevator-holo-uuid")) {
            String hologramUUID = shulkerBox.getMetadata("elevator-holo-uuid").get(0).asString();
            WrappedHologram hologram = getHologram(hologramUUID);
            if (hologram != null)
                return hologram;

            // It had the metadata, but the hologram could not be found.
            shulkerBox.removeMetadata("elevator-holo-uuid", Elevators.getInstance());
        }
        return null;
    }

    public static WrappedHologram getElevatorHologram(Elevator elevator) {

        if (!canUseHolograms())
            return null;

        WrappedHologram hologram = getElevatorHologramIfExists(elevator);
        if (hologram != null)
            return hologram;

        hologram = ElevatorHookService.getHologramHook().createHologram(elevator);

        updateElevatorHologram(elevator);
        return hologram;
    }

    public static void deleteHologram(Elevator elevator) {

        WrappedHologram hologram = getElevatorHologramIfExists(elevator);
        if (hologram == null)
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
        if (!canUseHolograms())
            return;

        for (BlockState state : PaperLib.getTileEntities(chunk, false).getTileEntities()) {
            if (!(state instanceof ShulkerBox))
                continue;
            ShulkerBox box = (ShulkerBox) state;

            ElevatorType elevatorType = ElevatorHelper.getElevatorType(box);
            if (elevatorType == null)
                continue;

            ElevatorHologramService.updateElevatorHologram(new Elevator(box, elevatorType));
        }
    }

    public static void updateElevatorHologram(Elevator elevator) {

        if (!canUseHolograms())
            return;

        if (elevator == null)
            return;

        List<String> hologramLines = elevator.getElevatorType().getHolographicLines().stream().map(i -> MessageHelper.formatPlaceholders(null, i)).collect(Collectors.toList());

        WrappedHologram hologram = hologramLines.isEmpty() ? getElevatorHologramIfExists(elevator) : getElevatorHologram(elevator);
        if (hologram == null) // We delete holograms that are empty. No need to "create" the hologram just to delete it.
            return;

        if (hologramLines.isEmpty()) {
            hologram.delete();
            return;
        }

        hologram.setLines(hologramLines);
        hologram.teleportTo(elevator.getLocation().clone().add(0.5, 1.5 + (hologram.getHeight()) / 2, 0.5));
    }

    public static void updateHologramsOfElevatorType(ElevatorType elevatorType) {
        List<? extends WrappedHologram> holograms = getHolograms().stream().filter(i -> i.getElevatorType().equals(elevatorType)).collect(Collectors.toList());
        for (WrappedHologram hologram : holograms)
            hologram.update();
    }

    public static UUID getNextAvailableUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (holograms.containsKey(uuid.toString()));

        return uuid;
    }

    public static void clearAll() {
        if (!canUseHolograms())
            return;

        new ArrayList<>(holograms.values()).forEach(WrappedHologram::delete);
    }

    public static void registerHologram(WrappedHologram holo) {
        if (!canUseHolograms())
            return;

        holograms.put(holo.getUUID(), holo);
        holo.getElevator().getShulkerBox().setMetadata("elevator-holo-uuid", new FixedMetadataValue(Elevators.getInstance(), holo.getUUID()));
    }

    public static void unregisterHologram(WrappedHologram hologram) {
        if (!canUseHolograms())
            return;

        holograms.remove(hologram.getUUID());

        Elevator elevator = hologram.getElevator();
        if (elevator != null && elevator.getShulkerBox() != null)
            elevator.getShulkerBox().removeMetadata("elevator-holo-uuid", Elevators.getInstance());
    }

    public static Collection<WrappedHologram> getHolograms() {
        if (!canUseHolograms())
            return new ArrayList<>();

        return holograms.values();
    }

    public static WrappedHologram getHologram(String uuid) {
        if (!canUseHolograms())
            return null;

        return holograms.get(uuid);
    }

    public static boolean canUseHolograms() {
        return ElevatorHookService.getHologramHook() != null && (ElevatorConfigService.isConfigLoaded() && ElevatorConfigService.getRootConfig().hologramServiceEnabled);
    }

}
