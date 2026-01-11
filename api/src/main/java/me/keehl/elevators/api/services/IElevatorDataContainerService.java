package me.keehl.elevators.api.services;

import me.keehl.elevators.api.models.IElevator;
import me.keehl.elevators.api.models.IElevatorType;
import me.keehl.elevators.api.util.persistantDataTypes.BooleanPersistentDataType;
import me.keehl.elevators.api.util.persistantDataTypes.LocaleComponentArrayDataType;
import me.keehl.elevators.api.util.persistantDataTypes.LocaleComponentDataType;
import me.keehl.elevators.api.util.persistantDataTypes.StringArrayDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.nio.charset.Charset;
import java.util.Optional;

public interface IElevatorDataContainerService extends IElevatorService {

    StringArrayDataType stringArrayPersistentDataType = new StringArrayDataType(Charset.defaultCharset());
    BooleanPersistentDataType booleanPersistentDataType = new BooleanPersistentDataType();
    LocaleComponentDataType localeComponentPersistentDataType = new LocaleComponentDataType();
    LocaleComponentArrayDataType localeComponentArrayPersistentDataType = new LocaleComponentArrayDataType();

    NamespacedKey createKey(String key);

    NamespacedKey getKeyFromKey(String keyKey, PersistentDataType<?,?> dataType);

    void dumpDataFromShulkerBoxIntoItem(ShulkerBox shulkerBox, ItemStack item);
    void dumpDataFromItemIntoShulkerBox(ShulkerBox shulkerBox, ItemStack item);
    void dumpDataFromItemIntoItem(ItemStack originItem, ItemStack destinationItem);

    String getElevatorKey(ItemStack item);

    String getElevatorKey(ShulkerBox box);

    <T> T getElevatorValue(ShulkerBox box, NamespacedKey key, T defaultValue);

    <Z> void setElevatorValue(ShulkerBox box, NamespacedKey key, Z value);

    void setElevatorKey(ItemStack item, IElevatorType type);

    ShulkerBox updateTypeKeyOnElevator(ShulkerBox box, IElevatorType type);

    ShulkerBox updateBox(ShulkerBox box, IElevatorType type);

    void updateItemStackFromV2(ItemStack item, IElevatorType type);

    Optional<String> getFloorNameOpt(IElevator elevator);

    String getFloorName(IElevator elevator);

    void setFloorName(IElevator elevator, String name);
}
