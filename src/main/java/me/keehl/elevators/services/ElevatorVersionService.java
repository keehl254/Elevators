package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.IElevatorVersionService;
import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.ShulkerBoxHelper;
import me.keehl.elevators.services.versions.ElevatorsV1;
import me.keehl.elevators.services.versions.ElevatorsV2;
import me.keehl.elevators.services.versions.ElevatorsV3;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ElevatorVersionService extends ElevatorService implements IElevatorVersionService {

    private final List<ElevatorVersion> versions = new ArrayList<>();

    private boolean initialized = false;

    public ElevatorVersionService(IElevators elevators) {
        super(elevators);
    }

    public void onInitialize() {
        if (this.initialized)
            return;
        ElevatorsAPI.pushAndHoldLog();

        this.versions.add(new ElevatorsV3());
        this.versions.add(new ElevatorsV2());
        this.versions.add(new ElevatorsV1());

        this.initialized = true;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Version service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public void onUninitialize() {

    }

    public IElevatorType getElevatorType(ItemStack item) {
        for (ElevatorVersion version : this.versions) {
            IElevatorType elevatorType = version.getElevatorType(item);
            if (elevatorType != null)
                return elevatorType;
        }
        return null;
    }

    public IElevatorType getElevatorType(ShulkerBox box, boolean updateBlock) {
        Map.Entry<IElevatorType, Function<ShulkerBox, ShulkerBox>> result = new AbstractMap.SimpleEntry<>(null, null);
        for (ElevatorVersion version : this.versions) {
            IElevatorType elevatorType = version.getElevatorType(box);
            if (elevatorType != null)
                result = new AbstractMap.SimpleEntry<>(elevatorType, version::convertToLaterVersion);
        }

        if (result.getKey() == null)
            return null;

        if (updateBlock) {
            ShulkerBox newBox = result.getValue().apply(box);
            newBox = ShulkerBoxHelper.clearContents(newBox);
            if (Elevators.getConfigService().getRootConfig().shouldForceFacingUpwards())
                ShulkerBoxHelper.setFacingUp(newBox);
        }
        return result.getKey();
    }

    public IElevatorType getElevatorType(Block block) {
        if (ItemStackHelper.isNotShulkerBox(block.getType()))
            return null;
        return getElevatorType(ShulkerBoxHelper.getShulkerBox(block), true);
    }

    public abstract static class ElevatorVersion {

        public abstract IElevatorType getElevatorType(ItemStack item);

        public abstract IElevatorType getElevatorType(ShulkerBox box);

        public abstract IElevatorType getElevatorType(Block block);

        public abstract ShulkerBox convertToLaterVersion(ShulkerBox box);

        protected IElevatorType getClassFromBoxName(String name) {
            if (name == null)
                return null;
            if (Elevators.getElevatorTypeService().doesElevatorTypeExist(name))
                return Elevators.getElevatorTypeService().getElevatorType(name);
            else
                return Elevators.getElevatorTypeService().getDefaultElevatorType();
        }

    }

}
