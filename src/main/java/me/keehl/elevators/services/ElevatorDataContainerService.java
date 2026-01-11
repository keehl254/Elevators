package me.keehl.elevators.services;

import me.keehl.elevators.Elevators;
import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.IElevators;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.services.IElevatorDataContainerService;
import me.keehl.elevators.helpers.ElevatorHelper;
import me.keehl.elevators.helpers.MessageHelper;
import me.keehl.elevators.api.models.IElevator;
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
public class ElevatorDataContainerService extends ElevatorService implements IElevatorDataContainerService {

    private final Map<String, Map.Entry<NamespacedKey, PersistentDataType<?,?>>> keyMap = new HashMap<>();

    private boolean initialized = false;

    private NamespacedKey typeKey;
    private NamespacedKey nameKey;

    public ElevatorDataContainerService(IElevators elevators) {
        super(elevators);
    }

    @Override
    public void onInitialize() {
        if(this.initialized)
            return;
        ElevatorsAPI.pushAndHoldLog();

        this.typeKey = getKeyFromKey("elevator-type", PersistentDataType.STRING);
        this.nameKey = getKeyFromKey("floor-name", PersistentDataType.STRING);

        this.initialized = true;
        ElevatorsAPI.popLog(logData -> ElevatorsAPI.log("Data Container service enabled. "+ ChatColor.YELLOW + "Took " + logData.getElapsedTime() + "ms"));
    }

    @Override
    public void onUninitialize() {

    }

    @Override
    public NamespacedKey createKey(String key) {
        return new NamespacedKey(Elevators.getInstance(), key);
    }

    @Override
    public NamespacedKey getKeyFromKey(String keyKey, PersistentDataType<?,?> dataType) { // keyKey, do you love me? Are you riding?
        keyKey = keyKey.toLowerCase();
        if(!this.keyMap.containsKey(keyKey))
            this.keyMap.put(keyKey, new AbstractMap.SimpleEntry<>(this.createKey(keyKey), dataType));

        return this.keyMap.get(keyKey).getKey();
    }

    @SuppressWarnings("unchecked")
    private void transferDataBetweenContainers(PersistentDataContainer from, PersistentDataContainer to) {
        for(String keyKey : this.keyMap.keySet()) {
            Map.Entry<NamespacedKey, PersistentDataType<?,?>> keyData = this.keyMap.get(keyKey);
            Object boxValue = from.get(keyData.getKey(), keyData.getValue());
            if(boxValue == null)
                continue;
            PersistentDataType<?, Object> boxType = (PersistentDataType<?, Object>) keyData.getValue();
            to.set(keyData.getKey(), boxType, boxValue);
        }
    }

    @Override
    public void dumpDataFromShulkerBoxIntoItem(ShulkerBox shulkerBox, ItemStack item) {
        if(!item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer boxDataContainer = shulkerBox.getPersistentDataContainer();
        PersistentDataContainer itemDataContainer = meta.getPersistentDataContainer();

        transferDataBetweenContainers(boxDataContainer,itemDataContainer);

        item.setItemMeta(meta);
    }

    @Override
    public void dumpDataFromItemIntoShulkerBox(ShulkerBox shulkerBox, ItemStack item) {
        if(!item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer boxDataContainer = shulkerBox.getPersistentDataContainer();
        PersistentDataContainer itemDataContainer = meta.getPersistentDataContainer();

        transferDataBetweenContainers(itemDataContainer, boxDataContainer);
    }

    @Override
    public void dumpDataFromItemIntoItem(ItemStack originItem, ItemStack destinationItem) {
        if(!originItem.hasItemMeta() || originItem.getType() != destinationItem.getType())
            return;
        destinationItem.setItemMeta(originItem.getItemMeta());
    }

    @Override
    public String getElevatorKey(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer tagContainer;
        if (meta != null) {
            tagContainer = meta.getPersistentDataContainer();

            if (tagContainer.has(this.typeKey, PersistentDataType.STRING))
                return tagContainer.get(this.typeKey, PersistentDataType.STRING);
        }
        return null;
    }

    @Override
    public String getElevatorKey(ShulkerBox box) {
        PersistentDataContainer tagContainer = box.getPersistentDataContainer();

        if (tagContainer.has(this.typeKey, PersistentDataType.STRING))
            return tagContainer.get(this.typeKey, PersistentDataType.STRING);
        return null;
    }

    @Override
    public <T> T getElevatorValue(ShulkerBox box, NamespacedKey key, T defaultValue) {
        Map.Entry<NamespacedKey, PersistentDataType<?,?>> keyData = this.keyMap.get(key.getKey());
        PersistentDataContainer dataContainer = box.getPersistentDataContainer();
        if(!dataContainer.has(keyData.getKey(), keyData.getValue()))
            return defaultValue;

        Object boxValue = dataContainer.get(keyData.getKey(), keyData.getValue());
        return (T) boxValue;
    }

    @Override
    public <Z> void setElevatorValue(ShulkerBox box, NamespacedKey key, Z value) {
        Map.Entry<NamespacedKey, PersistentDataType<?,?>> keyData = this.keyMap.get(key.getKey());
        PersistentDataContainer dataContainer = box.getPersistentDataContainer();

        if(value == null) {
            dataContainer.remove(keyData.getKey());
            return;
        }
        PersistentDataType<?,Z> dataType = (PersistentDataType<?, Z>) keyData.getValue();
        dataContainer.set(keyData.getKey(), dataType, value);
    }

    @Override
    public void setElevatorKey(ItemStack item, IElevatorType type) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null)
            meta.getPersistentDataContainer().set(this.typeKey, PersistentDataType.STRING, type.getTypeKey());
        item.setItemMeta(meta);
    }

    @Override
    public ShulkerBox updateTypeKeyOnElevator(ShulkerBox box, IElevatorType type) {
        box.getPersistentDataContainer().set(this.typeKey, PersistentDataType.STRING, type.getTypeKey());
        box.update();

        return box;
    }

    @Override
    public ShulkerBox updateBox(ShulkerBox box, IElevatorType type) {
        box.update(true);

        return this.updateTypeKeyOnElevator(box, type);
    }

    @Override
    public void updateItemStackFromV2(ItemStack item, IElevatorType type) {
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
        meta.getPersistentDataContainer().set(this.typeKey, PersistentDataType.STRING, type.getTypeKey());
        item.setItemMeta(meta);
    }

    @Override
    public Optional<String> getFloorNameOpt(IElevator elevator) {
        PersistentDataContainer tagContainer = elevator.getShulkerBox().getPersistentDataContainer();

        if (tagContainer.has(this.nameKey, PersistentDataType.STRING))
            return Optional.of(tagContainer.get(this.nameKey, PersistentDataType.STRING));

        return Optional.ofNullable(null);
    }

    @Override
    public String getFloorName(IElevator elevator) {
        return getFloorNameOpt(elevator).orElse("Floor #" + ElevatorHelper.getFloorNumberOrCount(elevator, true));
    }

    @Override
    public void setFloorName(IElevator elevator, String name) {
        PersistentDataContainer tagContainer = elevator.getShulkerBox().getPersistentDataContainer();
        if (name == null)
            tagContainer.remove(this.nameKey);
        else
            tagContainer.set(this.nameKey, PersistentDataType.STRING, name);
        elevator.getShulkerBox().update(true);
    }

}
