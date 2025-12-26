package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.models.Elevator;
import me.keehl.elevators.models.ElevatorType;
import me.keehl.elevators.util.persistantDataTypes.BooleanPersistentDataType;
import me.keehl.elevators.util.persistantDataTypes.StringArrayPersistentDataType;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class ElevatorDataContainerService {

    private static final Map<String, Map.Entry<NamespacedKey, PersistentDataType<?,?>>> keyMap = new HashMap<>();

    private static boolean initialized = false;

    private static NamespacedKey typeKey;
    private static NamespacedKey nameKey;

    public static StringArrayPersistentDataType stringArrayPersistentDataType = new StringArrayPersistentDataType();
    public static BooleanPersistentDataType booleanPersistentDataType = new BooleanPersistentDataType();

    public static void init() {
        if(ElevatorDataContainerService.initialized)
            return;
        Elevators.pushAndHoldLog();

        ElevatorDataContainerService.typeKey = getKeyFromKey("elevator-type", PersistentDataType.STRING);
        ElevatorDataContainerService.nameKey = getKeyFromKey("floor-name", PersistentDataType.STRING);

        ElevatorDataContainerService.initialized = true;
        Elevators.popLog(logData -> Elevators.log("Data Container service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    public static NamespacedKey createKey(String key) {
        return new NamespacedKey(Elevators.getInstance(), key);
    }

    public static NamespacedKey getKeyFromKey(String keyKey, PersistentDataType<?,?> dataType) { // keyKey, do you love me? Are you riding?
        keyKey = keyKey.toLowerCase();
        if(!keyMap.containsKey(keyKey))
            keyMap.put(keyKey, new AbstractMap.SimpleEntry<>(ElevatorDataContainerService.createKey(keyKey), dataType));

        return keyMap.get(keyKey).getKey();
    }
    @SuppressWarnings("unchecked")
    private static void transferDataBetweenContainers(PersistentDataContainer from, PersistentDataContainer to) {
        for(String keyKey : keyMap.keySet()) {
            Map.Entry<NamespacedKey, PersistentDataType<?,?>> keyData = keyMap.get(keyKey);
            Object boxValue = from.get(keyData.getKey(), keyData.getValue());
            if(boxValue == null)
                continue;
            PersistentDataType<?, Object> boxType = (PersistentDataType<?, Object>) keyData.getValue();
            to.set(keyData.getKey(), boxType, boxValue);
        }
    }

    public static void dumpDataFromShulkerBoxIntoItem(ShulkerBox shulkerBox, ItemStack item) {
        if(!item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer boxDataContainer = shulkerBox.getPersistentDataContainer();
        PersistentDataContainer itemDataContainer = meta.getPersistentDataContainer();

        transferDataBetweenContainers(boxDataContainer,itemDataContainer);

        item.setItemMeta(meta);
    }
    public static void dumpDataFromItemIntoShulkerBox(ShulkerBox shulkerBox, ItemStack item) {
        if(!item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer boxDataContainer = shulkerBox.getPersistentDataContainer();
        PersistentDataContainer itemDataContainer = meta.getPersistentDataContainer();

        transferDataBetweenContainers(itemDataContainer, boxDataContainer);
    }
    public static void dumpDataFromItemIntoItem(ItemStack originItem, ItemStack destinationItem) {
        if(!originItem.hasItemMeta() || originItem.getType() != destinationItem.getType())
            return;
        destinationItem.setItemMeta(originItem.getItemMeta());
    }

    public static String getElevatorKey(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer tagContainer;
        if (meta != null) {
            tagContainer = meta.getPersistentDataContainer();

            if (tagContainer.has(ElevatorDataContainerService.typeKey, PersistentDataType.STRING))
                return tagContainer.get(ElevatorDataContainerService.typeKey, PersistentDataType.STRING);
        }
        return null;
    }

    public static String getElevatorKey(ShulkerBox box) {
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();

        if (tagContainer.has(ElevatorDataContainerService.typeKey, PersistentDataType.STRING))
            return tagContainer.get(ElevatorDataContainerService.typeKey, PersistentDataType.STRING);
        return null;
    }

    public static <T> T getElevatorValue(ShulkerBox box, NamespacedKey key, T defaultValue) {
        Map.Entry<NamespacedKey, PersistentDataType<?,?>> keyData = keyMap.get(key.getKey());
        PersistentDataContainer dataContainer = box.getPersistentDataContainer();
        if(!dataContainer.has(keyData.getKey(), keyData.getValue()))
            return defaultValue;

        Object boxValue = dataContainer.get(keyData.getKey(), keyData.getValue());
        return (T) boxValue;
    }

    public static <Z> void setElevatorValue(ShulkerBox box, NamespacedKey key, Z value) {
        Map.Entry<NamespacedKey, PersistentDataType<?,?>> keyData = keyMap.get(key.getKey());
        PersistentDataContainer dataContainer = box.getPersistentDataContainer();

        if(value == null) {
            dataContainer.remove(keyData.getKey());
            return;
        }
        PersistentDataType<?,Z> dataType = (PersistentDataType<?, Z>) keyData.getValue();
        dataContainer.set(keyData.getKey(), dataType, value);
    }

    public static void setElevatorKey(ItemStack item, ElevatorType type) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
            meta.getPersistentDataContainer().set(ElevatorDataContainerService.typeKey, PersistentDataType.STRING, type.getTypeKey());
        item.setItemMeta(meta);
    }

    public static ShulkerBox updateTypeKeyOnElevator(ShulkerBox box, ElevatorType type) {
        box.getPersistentDataContainer().set(ElevatorDataContainerService.typeKey, PersistentDataType.STRING, type.getTypeKey());
        box.update();

        return box;
    }

    public static ShulkerBox updateBox(ShulkerBox box, ElevatorType type) {
        box.update(true);

        return ElevatorDataContainerService.updateTypeKeyOnElevator(box, type);
    }

    public static void updateItemStackFromV2(ItemStack item, ElevatorType type) {
        if (!item.hasItemMeta() || item.getItemMeta() == null)
            return;
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            String name = meta.getDisplayName();
            int sub = name.indexOf(MessageHelper.hideText("CoreEleKey:"));
            if (sub > -1) {
                name = name.substring(0, sub);
                meta.setDisplayName(name);
            }
        }
        meta.getPersistentDataContainer().set(ElevatorDataContainerService.typeKey, PersistentDataType.STRING, type.getTypeKey());
        item.setItemMeta(meta);
    }

    public static Optional<String> getFloorNameOpt(Elevator elevator) {
        PersistentDataContainer tagContainer = elevator.getShulkerBox().getPersistentDataContainer();

        if (tagContainer.has(ElevatorDataContainerService.nameKey, PersistentDataType.STRING))
            return Optional.of(tagContainer.get(ElevatorDataContainerService.nameKey, PersistentDataType.STRING));

        return Optional.ofNullable(null);
    }

    public static String getFloorName(Elevator elevator) {
        return getFloorNameOpt(elevator).orElse("Floor #" + ElevatorHelper.getFloorNumberOrCount(elevator, true));
    }

    public static void setFloorName(Elevator elevator, String name) {
        PersistentDataContainer tagContainer = elevator.getShulkerBox().getPersistentDataContainer();
        if (name == null)
            tagContainer.remove(ElevatorDataContainerService.nameKey);
        else
            tagContainer.set(ElevatorDataContainerService.nameKey, PersistentDataType.STRING, name);
        elevator.getShulkerBox().update(true);
    }

}
