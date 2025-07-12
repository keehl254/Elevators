package me.keehl.elevators.util.persistantDataTypes;

import me.keehl.elevators.services.ElevatorDataContainerService;
import org.bukkit.persistence.PersistentDataType;

public enum ElevatorsDataType {
    STRING(PersistentDataType.STRING),
    STRING_ARRAY(ElevatorDataContainerService.stringArrayPersistentDataType),
    BYTE(PersistentDataType.BYTE),
    BYTE_ARRAY(PersistentDataType.BYTE_ARRAY),
    BOOLEAN(ElevatorDataContainerService.booleanPersistentDataType),
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
