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

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class NameSpacedKeyService {

    private static Map<String, NamespacedKey> keyMap = new HashMap<>();

    private static boolean initialized = false;

    private static Elevators elevatorsInstance;

    private static NamespacedKey typeKey;
    private static NamespacedKey protectionKey;
    private static NamespacedKey nameKey;
    private static NamespacedKey instanceKey;

    public static void init(Elevators elevators) {
        if(NameSpacedKeyService.initialized)
            return;

        NameSpacedKeyService.elevatorsInstance = elevators;

        NameSpacedKeyService.typeKey = new NamespacedKey(elevators, "elevator-type");
        NameSpacedKeyService.protectionKey = new NamespacedKey(elevators, "supports-protection");
        NameSpacedKeyService.nameKey = new NamespacedKey(elevators, "floor-name");
        NameSpacedKeyService.instanceKey = new NamespacedKey(elevators, "instance-key");

        NameSpacedKeyService.initialized = true;
    }

    public static NamespacedKey getKeyFromKey(String keyKey) { // Do you love me? Are you riding?
        keyKey = keyKey.toLowerCase();
        if(!keyMap.containsKey(keyKey))
            keyMap.put(keyKey, new NamespacedKey(NameSpacedKeyService.elevatorsInstance, keyKey));

        return keyMap.get(keyKey);
    }

    public static String getElevatorKey(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer tagContainer;
        if (meta != null) {
            tagContainer = meta.getPersistentDataContainer();

            if (tagContainer.has(NameSpacedKeyService.typeKey, PersistentDataType.STRING))
                return tagContainer.get(NameSpacedKeyService.typeKey, PersistentDataType.STRING);
        }
        return null;
    }

    public static String getElevatorKey(ShulkerBox box) {
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();

        if (tagContainer.has(NameSpacedKeyService.typeKey, PersistentDataType.STRING))
            return tagContainer.get(NameSpacedKeyService.typeKey, PersistentDataType.STRING);
        return null;
    }

    public static boolean shouldElevatorBeGPProtected(ShulkerBox box) {
        if (!BaseElevators.supportsClaimProtection())
            return false;
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();

        if (tagContainer.has(NameSpacedKeyService.protectionKey, PersistentDataType.BYTE))
            return tagContainer.get(NameSpacedKeyService.protectionKey, PersistentDataType.BYTE) == 1;
        else
            tagContainer.set(NameSpacedKeyService.protectionKey, PersistentDataType.BYTE, (byte) (ConfigService.getRootConfig().claimProtectionDefault ? 1 : 0));
        return true;
    }

    public static boolean toggleGPProtectionOnElevator(ShulkerBox box) {
        if (!BaseElevators.supportsClaimProtection())
            return false;
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();
        byte current;

        if (tagContainer.has(NameSpacedKeyService.protectionKey, PersistentDataType.BYTE))
            current = tagContainer.get(NameSpacedKeyService.protectionKey, PersistentDataType.BYTE);
        else
            current = (byte) (ConfigService.getRootConfig().claimProtectionDefault ? 1 : 0);

        tagContainer.set(NameSpacedKeyService.protectionKey, PersistentDataType.BYTE, current == 1 ? 0 : (byte) 1);
        return true;
    }

    public static void setElevatorKey(ItemStack item, ElevatorType type) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
            meta.getPersistentDataContainer().set(NameSpacedKeyService.typeKey, PersistentDataType.STRING, type.getTypeKey());
        item.setItemMeta(meta);
    }

    public static ShulkerBox updateTypeKeyOnElevator(ShulkerBox box, ElevatorType type) {
        box.getPersistentDataContainer().set(NameSpacedKeyService.typeKey, PersistentDataType.STRING, type.getTypeKey());
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
        meta.getPersistentDataContainer().set(NameSpacedKeyService.typeKey, PersistentDataType.STRING, type.getTypeKey());
        item.setItemMeta(meta);
    }

    public static String getFloorName(ShulkerBox box, ElevatorType elevatorType) {
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();

        if (tagContainer.has(NameSpacedKeyService.nameKey, PersistentDataType.STRING))
            return tagContainer.get(NameSpacedKeyService.nameKey, PersistentDataType.STRING);
        else
            return "Floor #" + ElevatorHelper.getFloorNumberOrCount(box, elevatorType, true);
    }

    public static void setFloorName(ShulkerBox box, String name) {
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();
        if (name == null)
            tagContainer.remove(NameSpacedKeyService.nameKey);
        else
            tagContainer.set(NameSpacedKeyService.nameKey, PersistentDataType.STRING, name);
        box.update(true);
    }

}
