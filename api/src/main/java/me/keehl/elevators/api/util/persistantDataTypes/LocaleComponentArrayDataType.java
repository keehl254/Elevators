package me.keehl.elevators.api.util.persistantDataTypes;

import me.keehl.elevators.api.ElevatorsAPI;
import me.keehl.elevators.api.models.ILocaleComponent;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class LocaleComponentArrayDataType implements PersistentDataType<byte[], ILocaleComponent[]> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<ILocaleComponent[]> getComplexType() {
        return ILocaleComponent[].class;
    }

    @Override
    public byte @NotNull [] toPrimitive(final ILocaleComponent[] components, @NotNull final PersistentDataAdapterContext context) {

        final byte[][] allStringBytes = new byte[components.length][];
        int total = 0;
        for (int i = 0; i < allStringBytes.length; i++) {
            final byte[] bytes = components[i].serialize().getBytes();
            allStringBytes[i] = bytes;
            total += bytes.length;
        }

        final ByteBuffer buffer = ByteBuffer.allocate(total + allStringBytes.length * 4); //stores integers
        for (final byte[] bytes : allStringBytes) {
            buffer.putInt(bytes.length);
            buffer.put(bytes);
        }

        return buffer.array();
    }

    @NotNull
    @Override
    public ILocaleComponent @NotNull [] fromPrimitive(final byte @NotNull [] bytes, @NotNull final PersistentDataAdapterContext itemTagAdapterContext) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        final List<ILocaleComponent> list = new ArrayList<>();

        while (buffer.remaining() > 0) {
            if (buffer.remaining() < 4) break;
            final int stringLength = buffer.getInt();
            if (buffer.remaining() < stringLength) break;

            final byte[] stringBytes = new byte[stringLength];
            buffer.get(stringBytes);

            list.add(ElevatorsAPI.getElevators().createComponentFromText(new String(stringBytes)));
        }

        return list.toArray(new ILocaleComponent[]{});
    }
}