package xfacthd.contex.client.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.state.ConnectionDirection;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.type.OcclusionMode;
import xfacthd.contex.api.utils.Constants;

public sealed class SimpleTextureType extends FullTextureType permits OmniPillarTextureType
{
    private static final ConnectionDirection[] CARDINAL_DIRECTIONS = new ConnectionDirection[] {
            ConnectionDirection.UP, ConnectionDirection.RIGHT, ConnectionDirection.DOWN, ConnectionDirection.LEFT
    };

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
                connections = set(connections, dir);
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
            if (isSet(connections, ConnectionDirection.UP) && isSet(connections, ConnectionDirection.LEFT))
            {
                connections = set(connections, ConnectionDirection.UP_LEFT);
            }
            if (isSet(connections, ConnectionDirection.DOWN) && isSet(connections, ConnectionDirection.LEFT))
            {
                connections = set(connections, ConnectionDirection.DOWN_LEFT);
            }
            if (isSet(connections, ConnectionDirection.UP) && isSet(connections, ConnectionDirection.RIGHT))
            {
                connections = set(connections, ConnectionDirection.UP_RIGHT);
            }
            if (isSet(connections, ConnectionDirection.DOWN) && isSet(connections, ConnectionDirection.RIGHT))
            {
                connections = set(connections, ConnectionDirection.DOWN_RIGHT);
            }
            stateMap[side.ordinal()] = connections;
        }
    }
}
