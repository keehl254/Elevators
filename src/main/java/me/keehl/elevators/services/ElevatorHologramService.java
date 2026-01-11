package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.models.hooks.IElevatorHologram;
import me.keehl.elevators.api.models.hooks.IWrappedHologram;
import me.keehl.elevators.api.services.IElevatorHologramService;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.VersionHelper;
import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.models.Elevator;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.keehl.elevators.models.hooks.WrappedHologram;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.stream.Collectors;

public class ElevatorHologramService extends ElevatorService implements IElevatorHologramService {

    private boolean initialized = false;

    private final Map<String, IWrappedHologram> holograms = new HashMap<>();

    private WrappedTask task;
    private int currentIndex = 0;

    public ElevatorHologramService(IElevators elevators) {
        super(elevators);
    }

    @Override
    public void onInitialize() {
        if (this.initialized)
            return;
        ElevatorsAPI.pushAndHoldLog();

        this.task = Elevators.getFoliaLib().getScheduler().runTimer(() -> {
            if (!canUseHolograms())
                return;

            IWrappedHologram[] holograms = getHolograms().toArray(new IWrappedHologram[]{});
            if (holograms.length == 0)
                return;

            // Try to update 10 holos at a time.
            int attempts = 0;
            int startIndex = this.currentIndex;
            while (attempts++ < 10) {
                this.currentIndex = this.currentIndex % holograms.length;
                IWrappedHologram hologram = holograms[this.currentIndex];
                if (!hologram.getElevatorLocation().getChunk().isLoaded())
                    continue;

                hologram.update();
                this.currentIndex++;

                // We've come full circle looking for 10 holos to update.
                if (startIndex == this.currentIndex)
                    return;
            }

        }, 5, 5);

        this.initialized = true;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Hologram service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    @Override
    public void onUninitialize() {
        clearAll();
        this.task.cancel();
    }

    public IWrappedHologram getElevatorHologramIfExists(IElevator elevator) {

        ShulkerBox shulkerBox = elevator.getShulkerBox();
        if (shulkerBox.hasMetadata("elevator-holo-uuid")) {
            String hologramUUID = shulkerBox.getMetadata("elevator-holo-uuid").get(0).asString();
            IWrappedHologram hologram = getHologram(hologramUUID);
            if (hologram != null)
                return hologram;

            // It had the metadata, but the hologram could not be found.
            shulkerBox.removeMetadata("elevator-holo-uuid", Elevators.getInstance());
        }
        return null;
    }

    public IWrappedHologram getElevatorHologram(IElevator elevator) {

        if (!canUseHolograms())
            return null;

        IWrappedHologram hologram = getElevatorHologramIfExists(elevator);
        if (hologram != null)
            return hologram;

        UUID nextUUID = this.getNextAvailableUUID();
        IElevatorHologram newHologram = Elevators.getHooksService().getHologramHook().createHologram(this.getNextAvailableUUID(), elevator);
        hologram = new WrappedHologram(nextUUID, newHologram, elevator);

        updateElevatorHologram(elevator);
        return hologram;
    }

    public void deleteHologram(IElevator elevator) {

        IWrappedHologram hologram = getElevatorHologramIfExists(elevator);
        if (hologram == null)
            return;

        hologram.delete();
    }

    public void deleteHologramsInChunk(Chunk ignoredChunk) {

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

    public void updateHologramsInChunk(Chunk chunk) {
        if (!canUseHolograms())
            return;

        Collection<BlockState> tileEntities = VersionHelper.getShulkerBoxesInChunk(chunk);
        for (BlockState state : tileEntities) {
            if (!(state instanceof ShulkerBox))
                continue;
            ShulkerBox box = (ShulkerBox) state;

            IElevatorType elevatorType = ElevatorHelper.getElevatorType(box);
            if (elevatorType == null)
                continue;

            this.updateElevatorHologram(new Elevator(box, elevatorType));
        }
    }

    public void updateElevatorHologram(IElevator elevator) {

        if (!canUseHolograms())
            return;

        if (elevator == null)
            return;

        List<String> hologramLines = elevator.getElevatorType().getHolographicLines().stream().map(ILocaleComponent::toLegacyText).collect(Collectors.toList());

        IWrappedHologram hologram = hologramLines.isEmpty() ? getElevatorHologramIfExists(elevator) : getElevatorHologram(elevator);
        if (hologram == null) // We delete holograms that are empty. No need to "create" the hologram just to delete it.
            return;

        if (hologramLines.isEmpty()) {
            hologram.delete();
            return;
        }

        hologram.setLines(hologramLines);
        hologram.teleportTo(elevator.getLocation().clone().add(0.5, 1.5 + (hologram.getHeight()) / 2, 0.5));
    }

    public void updateHologramsOfElevatorType(IElevatorType elevatorType) {
        List<IWrappedHologram> holograms = getHolograms().stream().filter(i -> i.getElevatorType().equals(elevatorType)).collect(Collectors.toList());
        for (IWrappedHologram hologram : holograms) {
            hologram.update();
        }
    }

    public UUID getNextAvailableUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (this.holograms.containsKey(uuid.toString()));

        return uuid;
    }

    public void clearAll() {
        if (!canUseHolograms())
            return;

        new ArrayList<>(this.holograms.values()).forEach(IWrappedHologram::delete);
    }

    public void registerHologram(WrappedHologram holo) {
        if (!canUseHolograms())
            return;

        this.holograms.put(holo.getUUID(), holo);
        holo.getElevatorRecord().getShulkerBox().setMetadata("elevator-holo-uuid", new FixedMetadataValue(Elevators.getInstance(), holo.getUUID()));
    }

    public void unregisterHologram(WrappedHologram hologram) {
        if (!canUseHolograms())
            return;

        this.holograms.remove(hologram.getUUID());

        IElevator elevator = hologram.getElevatorRecord();
        if (elevator != null && elevator.getShulkerBox() != null)
            elevator.getShulkerBox().removeMetadata("elevator-holo-uuid", Elevators.getInstance());
    }

    public Collection<IWrappedHologram> getHolograms() {
        if (!canUseHolograms())
            return new ArrayList<>();

        return this.holograms.values();
    }

    public IWrappedHologram getHologram(String uuid) {
        if (!canUseHolograms())
            return null;

        return this.holograms.get(uuid);
    }

    public boolean canUseHolograms() {
        return Elevators.getHooksService().getHologramHook() != null && (Elevators.getConfigService().isConfigLoaded() && Elevators.getConfigService().getRootConfig().isHologramServiceEnabled());
    }

}
