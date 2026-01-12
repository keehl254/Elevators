package me.keehl.elevators.api.util.config.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface IFieldData {

    Field getField();

    Class<?> getFieldClass();

    Type getFieldType();

    IFieldData[] getGenericData() throws ClassNotFoundException;

}