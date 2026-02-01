package me.keehl.elevators.api.util.persistantDataTypes;

import me.keehl.elevators.api.models.ILocaleComponent;
import me.keehl.elevators.api.services.IElevatorDataContainerService;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ElevatorsDataType {
    public static PersistentDataType<?, String> STRING = PersistentDataType.STRING;
    public static PersistentDataType<?, String[]> STRING_ARRAY = IElevatorDataContainerService.stringArrayPersistentDataType;
    public static PersistentDataType<?, ILocaleComponent> LOCALE_COMPONENT = IElevatorDataContainerService.localeComponentPersistentDataType;
    public static PersistentDataType<?, ILocaleComponent[]> LOCALE_COMPONENT_ARRAY = IElevatorDataContainerService.localeComponentArrayPersistentDataType;
    public static PersistentDataType<?, Byte> BYTE = PersistentDataType.BYTE;
    public static PersistentDataType<byte[], byte[]> BYTE_ARRAY = PersistentDataType.BYTE_ARRAY;
    public static PersistentDataType<?, Boolean> BOOLEAN = (IElevatorDataContainerService.booleanPersistentDataType);
    public static PersistentDataType<?, Double> DOUBLE = (PersistentDataType.DOUBLE);
    public static PersistentDataType<?, Float> FLOAT = (PersistentDataType.FLOAT);
    public static PersistentDataType<?, Short> SHORT = (PersistentDataType.SHORT);
    public static PersistentDataType<?, Integer> INT = (PersistentDataType.INTEGER);
    public static PersistentDataType<int[], int[]> INT_ARRAY = (PersistentDataType.INTEGER_ARRAY);
    public static PersistentDataType<?, Long> LONG = (PersistentDataType.LONG);
    public static PersistentDataType<long[], long[]> LONG_ARRAY = (PersistentDataType.LONG_ARRAY);
    public static PersistentDataType<PersistentDataContainer, PersistentDataContainer> TAG_CONTAINER = (PersistentDataType.TAG_CONTAINER);
}
