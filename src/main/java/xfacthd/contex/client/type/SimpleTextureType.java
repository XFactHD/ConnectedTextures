package xfacthd.contex.client.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.state.ConnectionDirection;
import xfacthd.contex.api.state.ConnectionState;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.utils.Constants;

import java.util.Map;

public sealed class SimpleTextureType extends FullTextureType permits OmniPillarTextureType
{
    private static final ConnectionDirection[] CARDINAL_DIRECTIONS = new ConnectionDirection[] {
            ConnectionDirection.UP, ConnectionDirection.RIGHT, ConnectionDirection.DOWN, ConnectionDirection.LEFT
    };

    @Override
    public ConnectionState getConnectionState(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            ResourceLocation texture
    )
    {
        byte connections = 0;
        for (ConnectionDirection dir : CARDINAL_DIRECTIONS)
        {
            BlockPos otherPos = pos.offset(dir.getOffset(side));
            if (!predicate.test(level, pos, otherPos, state, dir, dir, side, side))
            {
                continue;
            }

            BlockPos otherPosOff = otherPos.relative(side);
            if (!predicate.test(level, pos, otherPosOff, state, dir, dir.mapToOppositeFace(side), side, side.getOpposite()))
            {
                connections |= (byte) (1 << dir.ordinal());
            }
        }

        return new ConnectionState(this, texture, connections);
    }

    @Override
    public void postProcessConnections(Map<Direction, ConnectionState> stateMap)
    {
        for (Direction side : Constants.DIRECTIONS)
        {
            ConnectionState state = stateMap.get(side);
            byte connections = state.connections();
            boolean changed = false;

            if (isSet(connections, ConnectionDirection.UP) && isSet(connections, ConnectionDirection.LEFT))
            {
                connections |= (byte) (1 << ConnectionDirection.UP_LEFT.ordinal());
                changed = true;
            }
            if (isSet(connections, ConnectionDirection.DOWN) && isSet(connections, ConnectionDirection.LEFT))
            {
                connections |= (byte) (1 << ConnectionDirection.DOWN_LEFT.ordinal());
                changed = true;
            }
            if (isSet(connections, ConnectionDirection.UP) && isSet(connections, ConnectionDirection.RIGHT))
            {
                connections |= (byte) (1 << ConnectionDirection.UP_RIGHT.ordinal());
                changed = true;
            }
            if (isSet(connections, ConnectionDirection.DOWN) && isSet(connections, ConnectionDirection.RIGHT))
            {
                connections |= (byte) (1 << ConnectionDirection.DOWN_RIGHT.ordinal());
                changed = true;
            }

            if (changed)
            {
                stateMap.put(side, new ConnectionState(state.type(), state.texture(), connections));
            }
        }
    }

    private static boolean isSet(byte connections, ConnectionDirection dir)
    {
        return (connections & (1 << dir.ordinal())) != 0;
    }
}
