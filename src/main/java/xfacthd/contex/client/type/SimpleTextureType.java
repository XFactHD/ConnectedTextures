package xfacthd.contex.client.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.state.ConnectionDirection;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.type.OcclusionMode;
import xfacthd.contex.api.utils.Constants;

public sealed class SimpleTextureType extends FullTextureType permits OmniPillarTextureType, SimpleCarpetTextureType
{
    private static final ConnectionDirection[] CARDINAL_DIRECTIONS = new ConnectionDirection[] {
            ConnectionDirection.UP, ConnectionDirection.RIGHT, ConnectionDirection.DOWN, ConnectionDirection.LEFT
    };
    public static final SimpleTextureType INSTANCE = new SimpleTextureType();

    protected SimpleTextureType() { }

    @Override
    public byte getConnectionState(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            OcclusionMode occlusionMode
    )
    {
        byte connections = 0;
        for (ConnectionDirection dir : CARDINAL_DIRECTIONS)
        {
            BlockPos otherPos = pos.offset(dir.getOffset(side));
            if (!predicate.test(level, pos, otherPos, state, side, side))
            {
                continue;
            }

            if (isConnectionVisible(level, otherPos, side, predicate, occlusionMode))
            {
                connections = dir.set(connections);
            }
        }

        return connections;
    }

    @Override
    public void postProcessConnections(byte[] stateMap)
    {
        for (Direction side : Constants.DIRECTIONS)
        {
            byte connections = stateMap[side.ordinal()];
            if (ConnectionDirection.UP.isSet(connections) && ConnectionDirection.LEFT.isSet(connections))
            {
                connections = ConnectionDirection.UP_LEFT.set(connections);
            }
            if (ConnectionDirection.DOWN.isSet(connections) && ConnectionDirection.LEFT.isSet(connections))
            {
                connections = ConnectionDirection.DOWN_LEFT.set(connections);
            }
            if (ConnectionDirection.UP.isSet(connections) && ConnectionDirection.RIGHT.isSet(connections))
            {
                connections = ConnectionDirection.UP_RIGHT.set(connections);
            }
            if (ConnectionDirection.DOWN.isSet(connections) && ConnectionDirection.RIGHT.isSet(connections))
            {
                connections = ConnectionDirection.DOWN_RIGHT.set(connections);
            }
            stateMap[side.ordinal()] = connections;
        }
    }
}
