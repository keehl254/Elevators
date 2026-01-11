package me.keehl.elevators.api.util.persistantDataTypes;

import me.keehl.elevators.api.services.IElevatorDataContainerService;
import org.bukkit.persistence.PersistentDataType;

public enum ElevatorsDataType {
    STRING(PersistentDataType.STRING),
    STRING_ARRAY(IElevatorDataContainerService.stringArrayPersistentDataType),
    LOCALE_COMPONENT(IElevatorDataContainerService.localeComponentPersistentDataType),
    LOCALE_COMPONENT_ARRAY(IElevatorDataContainerService.localeComponentArrayPersistentDataType),
    BYTE(PersistentDataType.BYTE),
    BYTE_ARRAY(PersistentDataType.BYTE_ARRAY),
    BOOLEAN(IElevatorDataContainerService.booleanPersistentDataType),
    DOUBLE(PersistentDataType.DOUBLE),
    FLOAT(PersistentDataType.FLOAT),
    SHORT(PersistentDataType.SHORT),
    INT(PersistentDataType.INTEGER),
    INT_ARRAY(PersistentDataType.INTEGER_ARRAY),
    LONG(PersistentDataType.LONG),
    LONG_ARRAY(PersistentDataType.LONG_ARRAY),
    TAG_CONTAINER(PersistentDataType.TAG_CONTAINER);

    private final PersistentDataType<?,?> dataType;

    ElevatorsDataType(PersistentDataType<?,?> dataType) {
        this.dataType = dataType;
    }

    public <T> PersistentDataType<?, T> getDataType() {
        return (PersistentDataType<?, T>) this.dataType;
    }

}
