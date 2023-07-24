package com.lkeehl.elevators.services;

import com.lkeehl.elevators.Elevators;
import com.lkeehl.elevators.helpers.ElevatorHelper;
import com.lkeehl.elevators.models.ElevatorType;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class DataContainerService {

    private static Map<String, Map.Entry<NamespacedKey, PersistentDataType<?,?>>> keyMap = new HashMap<>();

    private static boolean initialized = false;

    private static Elevators elevatorsInstance;

    private static NamespacedKey typeKey;
    private static NamespacedKey protectionKey;
    private static NamespacedKey nameKey;
    private static NamespacedKey instanceKey;

    public static void init(Elevators elevators) {
        if(DataContainerService.initialized)
            return;

        DataContainerService.elevatorsInstance = elevators;

        DataContainerService.typeKey = getKeyFromKey("elevator-type", PersistentDataType.STRING);
        DataContainerService.protectionKey = getKeyFromKey("supports-protection", PersistentDataType.BYTE);
        DataContainerService.nameKey = getKeyFromKey("floor-name", PersistentDataType.STRING);
        DataContainerService.instanceKey = new NamespacedKey(elevators, "instance-key");

        DataContainerService.initialized = true;
    }

    public static NamespacedKey getKeyFromKey(String keyKey, PersistentDataType<?,?> dataType) { // Do you love me? Are you riding?
        keyKey = keyKey.toLowerCase();
        if(!keyMap.containsKey(keyKey))
            keyMap.put(keyKey, new AbstractMap.SimpleEntry<>(new NamespacedKey(DataContainerService.elevatorsInstance, keyKey), dataType));

        return keyMap.get(keyKey).getKey();
    }
    @SuppressWarnings("unchecked")
    private static void transferDataBetweenContainers(PersistentDataContainer from, PersistentDataContainer to) {
        for(String keyKey : keyMap.keySet()) {
            Map.Entry<NamespacedKey, PersistentDataType<?,?>> keyData = keyMap.get(keyKey);
            Object boxValue = from.get(keyData.getKey(), keyData.getValue());
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

    public static String getElevatorKey(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer tagContainer;
        if (meta != null) {
            tagContainer = meta.getPersistentDataContainer();

            if (tagContainer.has(DataContainerService.typeKey, PersistentDataType.STRING))
                return tagContainer.get(DataContainerService.typeKey, PersistentDataType.STRING);
        }
        return null;
    }

    public static String getElevatorKey(ShulkerBox box) {
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();

        if (tagContainer.has(DataContainerService.typeKey, PersistentDataType.STRING))
            return tagContainer.get(DataContainerService.typeKey, PersistentDataType.STRING);
        return null;
    }

    public static boolean shouldElevatorBeGPProtected(ShulkerBox box) {
        if (!BaseElevators.supportsClaimProtection())
            return false;
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();

        if (tagContainer.has(DataContainerService.protectionKey, PersistentDataType.BYTE))
            return tagContainer.get(DataContainerService.protectionKey, PersistentDataType.BYTE) == 1;
        else
            tagContainer.set(DataContainerService.protectionKey, PersistentDataType.BYTE, (byte) (ConfigService.getRootConfig().claimProtectionDefault ? 1 : 0));
        return true;
    }

    public static boolean toggleGPProtectionOnElevator(ShulkerBox box) {
        if (!BaseElevators.supportsClaimProtection())
            return false;
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();
        byte current;

        if (tagContainer.has(DataContainerService.protectionKey, PersistentDataType.BYTE))
            current = tagContainer.get(DataContainerService.protectionKey, PersistentDataType.BYTE);
        else
            current = (byte) (ConfigService.getRootConfig().claimProtectionDefault ? 1 : 0);

        tagContainer.set(DataContainerService.protectionKey, PersistentDataType.BYTE, current == 1 ? 0 : (byte) 1);
        return true;
    }

    public static void setElevatorKey(ItemStack item, ElevatorType type) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
            meta.getPersistentDataContainer().set(DataContainerService.typeKey, PersistentDataType.STRING, type.getTypeKey());
        item.setItemMeta(meta);
    }

    public static ShulkerBox updateTypeKeyOnElevator(ShulkerBox box, ElevatorType type) {
        box.getPersistentDataContainer().set(DataContainerService.typeKey, PersistentDataType.STRING, type.getTypeKey());
        box.update();

        return box;
    }

    public static ShulkerBox updateBox(ShulkerBox box, ElevatorType type) {
        box.update(true);

        return TagHelper.updateTypeKeyOnElevator(box, type);
    }

    public static void updateItemStackFromV2(ItemStack item, ElevatorType type) {
        if (!item.hasItemMeta() || item.getItemMeta() == null)
            return;
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            String name = meta.getDisplayName();
            int sub = name.indexOf(BaseUtil.hideText("CoreEleKey:"));
            if (sub > -1) {
                name = name.substring(0, sub);
                meta.setDisplayName(name);
            }
        }
        meta.getPersistentDataContainer().set(DataContainerService.typeKey, PersistentDataType.STRING, type.getTypeKey());
        item.setItemMeta(meta);
    }

    public static String getFloorName(ShulkerBox box, ElevatorType elevatorType) {
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();

        if (tagContainer.has(DataContainerService.nameKey, PersistentDataType.STRING))
            return tagContainer.get(DataContainerService.nameKey, PersistentDataType.STRING);
        else
            return "Floor #" + ElevatorHelper.getFloorNumberOrCount(box, elevatorType, true);
    }

    public static void setFloorName(ShulkerBox box, String name) {
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();
        if (name == null)
            tagContainer.remove(DataContainerService.nameKey);
        else
            tagContainer.set(DataContainerService.nameKey, PersistentDataType.STRING, name);
        box.update(true);
    }

}
