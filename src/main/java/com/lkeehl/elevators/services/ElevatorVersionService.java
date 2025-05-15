package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ItemStackHelper;
import com.lkeehl.elevators.helpers.ShulkerBoxHelper;
import com.lkeehl.elevators.models.ElevatorType;
import com.lkeehl.elevators.services.versions.ElevatorsV1;
import com.lkeehl.elevators.services.versions.ElevatorsV2;
import com.lkeehl.elevators.services.versions.ElevatorsV3;
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

    private static <T> Map.Entry<ElevatorType, Function<ShulkerBox, ShulkerBox>> executeAllAndGetUpdateFunc(T arg, Function<ElevatorVersion, Function<T, ElevatorType>> func) {
        for (ElevatorVersion version : versions) {
            ElevatorType obj = func.apply(version).apply(arg);
            if (obj != null)
                return new AbstractMap.SimpleEntry<>(obj, version::convertToLaterVersion);
        }

        return new AbstractMap.SimpleEntry<>(null, null);
    }

    public static ElevatorType getElevatorType(ItemStack item) {
        return executeAllAndGetUpdateFunc(item, version -> version::getElevatorType).getKey();
    }

    public static ElevatorType getElevatorType(ShulkerBox box, boolean updateBlock) {
        Map.Entry<ElevatorType, Function<ShulkerBox, ShulkerBox>> result = executeAllAndGetUpdateFunc(box, version -> version::getElevatorType);
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
