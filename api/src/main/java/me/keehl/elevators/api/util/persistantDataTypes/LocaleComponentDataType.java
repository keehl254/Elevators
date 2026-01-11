package me.keehl.elevators.api.util.persistantDataTypes;

import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.ILocaleComponent;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class LocaleComponentDataType implements PersistentDataType<byte[], ILocaleComponent> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<ILocaleComponent> getComplexType() {
        return ILocaleComponent.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(final ILocaleComponent component, @NotNull final PersistentDataAdapterContext context) {
        return component.serialize().getBytes();
    }

    @NotNull
    @Override
    public ILocaleComponent fromPrimitive(final byte @NotNull [] bytes, @NotNull final PersistentDataAdapterContext itemTagAdapterContext) {
        return ElevatorsAPI.getElevators().createComponentFromText(new String(bytes));
    }
}