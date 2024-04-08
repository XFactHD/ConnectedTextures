package xfacthd.contex.client.data;

import net.minecraft.core.Direction;

import java.util.*;

public final class ConnectionStateContainer
{
    private final byte[][] states = new byte[6][];
    private final int metaCount;

    public ConnectionStateContainer(int metaCount)
    {
        this.metaCount = metaCount;
    }

    public byte[] get(Direction side)
    {
        return states[side.ordinal()];
    }

    public void put(Direction side, int idx, byte conState)
    {
        byte[] sideStates = states[side.ordinal()];
        if (sideStates == null)
        {
            sideStates = states[side.ordinal()] = new byte[metaCount];
        }
        sideStates[idx] = conState;
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode(states);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        return Arrays.deepEquals(states, ((ConnectionStateContainer) o).states);
    }
}
