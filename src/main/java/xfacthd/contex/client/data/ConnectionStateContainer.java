package xfacthd.contex.client.data;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.client.model.ConTexModel;

import java.util.Arrays;
import java.util.Objects;

public final class ConnectionStateContainer
{
    private final ConTexModel owningModel;
    private final byte[][] states = new byte[6][];
    private final int metaCount;

    public ConnectionStateContainer(ConTexModel owningModel, int metaCount)
    {
        this.owningModel = owningModel;
        this.metaCount = metaCount;
    }

    public byte @Nullable[] get(Direction side)
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
        return Objects.hash(owningModel, Arrays.deepHashCode(states));
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ConnectionStateContainer that)) { return false; }
        return owningModel == that.owningModel && Objects.deepEquals(states, that.states);
    }
}
