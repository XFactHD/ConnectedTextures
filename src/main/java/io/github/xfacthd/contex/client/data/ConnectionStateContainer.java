package io.github.xfacthd.contex.client.data;

import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;
import io.github.xfacthd.contex.client.model.ConTexModel;

import java.util.Arrays;
import java.util.Objects;

public final class ConnectionStateContainer
{
    private final ConTexModel owningModel;
    private final long[] states;
    @Nullable
    private final Object delegateGeometryKey;

    public ConnectionStateContainer(ConTexModel owningModel, int metaCount, @Nullable Object delegateGeometryKey)
    {
        this.owningModel = owningModel;
        this.states = new long[metaCount];
        this.delegateGeometryKey = delegateGeometryKey;
    }

    public byte get(Direction side, int metaIdx)
    {
        return (byte) (states[metaIdx] >> (side.ordinal() * 8) & 0xFF);
    }

    public void put(Direction side, int metaIdx, byte conState)
    {
        long mask = 0xFFL << (side.ordinal() * 8);
        long shiftedState = Byte.toUnsignedLong(conState) << (side.ordinal() * 8);
        long packed = states[metaIdx];
        packed = packed & ~mask | shiftedState;
        states[metaIdx] = packed;
    }

    @Override
    public int hashCode()
    {
        return (owningModel.hashCode() * 31 + Arrays.hashCode(states)) * 31 + Objects.hashCode(delegateGeometryKey);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ConnectionStateContainer that)) { return false; }
        return owningModel == that.owningModel &&
                Arrays.equals(states, that.states) &&
                Objects.equals(delegateGeometryKey, that.delegateGeometryKey);
    }
}
