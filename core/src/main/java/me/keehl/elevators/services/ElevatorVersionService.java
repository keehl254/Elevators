package me.keehl.elevators.services;

import me.keehl.elevators.helpers.ItemStackHelper;
import me.keehl.elevators.helpers.ShulkerBoxHelper;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.services.versions.ElevatorsV1;
import me.keehl.elevators.services.versions.ElevatorsV2;
import me.keehl.elevators.services.versions.ElevatorsV3;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ElevatorVersionService {

    private static final List<ElevatorVersion> versions = new ArrayList<>();

    private static boolean initialized = false;

    public static void init() {
        if (ElevatorVersionService.initialized)
            return;

        versions.add(new ElevatorsV3());
        versions.add(new ElevatorsV2());
        versions.add(new ElevatorsV1());

        ElevatorVersionService.initialized = true;
    }

    public static ElevatorType getElevatorType(ItemStack item) {
        for (ElevatorVersion version : versions) {
            ElevatorType elevatorType = version.getElevatorType(item);
            if (elevatorType != null)
                return elevatorType;
        }
        return null;
    }

    public static ElevatorType getElevatorType(ShulkerBox box, boolean updateBlock) {
        Map.Entry<ElevatorType, Function<ShulkerBox, ShulkerBox>> result = new AbstractMap.SimpleEntry<>(null, null);
        for (ElevatorVersion version : versions) {
            ElevatorType elevatorType = version.getElevatorType(box);
            if (elevatorType != null)
                result = new AbstractMap.SimpleEntry<>(elevatorType, version::convertToLaterVersion);
        }

        if (result.getKey() == null)
            return null;

        if (updateBlock) {
            ShulkerBox newBox = result.getValue().apply(box);
            newBox = ShulkerBoxHelper.clearContents(newBox);
            if (ElevatorConfigService.getRootConfig().forceFacingUpwards)
                ShulkerBoxHelper.setFacingUp(newBox);
        }
        return result.getKey();
    }

    public static ElevatorType getElevatorType(Block block) {
        if (ItemStackHelper.isNotShulkerBox(block.getType()))
            return null;
        return getElevatorType(ShulkerBoxHelper.getShulkerBox(block), true);
    }

    public abstract static class ElevatorVersion {

        public abstract ElevatorType getElevatorType(ItemStack item);

        public abstract ElevatorType getElevatorType(ShulkerBox box);

        public abstract ElevatorType getElevatorType(Block block);

        public abstract ShulkerBox convertToLaterVersion(ShulkerBox box);

        protected ElevatorType getClassFromBoxName(String name) {
            if (name == null)
                return null;
            if (ElevatorTypeService.doesElevatorTypeExist(name))
                return ElevatorTypeService.getElevatorType(name);
            else
                return ElevatorTypeService.getDefaultElevatorType();
        }

    }

}
