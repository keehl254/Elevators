package com.lkeehl.elevators.util.persistantDataTypes;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class StringArrayPersistentDataType implements PersistentDataType<String, String[]> {

    @Override
    @NotNull
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<String[]> getComplexType() {
        return String[].class;
    }

    @Override
    public @NotNull String toPrimitive(String @NotNull [] strings, @NotNull PersistentDataAdapterContext context) {
        return String.join("\n", strings);
    }

    @Override
    public @NotNull String @NotNull [] fromPrimitive(String primitive, @NotNull PersistentDataAdapterContext context) {
        return primitive.split("\n");
    }
}
