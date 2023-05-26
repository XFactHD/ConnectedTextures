package xfacthd.contex.client.type;

import net.minecraft.core.Direction;
import xfacthd.contex.api.state.*;
import xfacthd.contex.api.type.UV;

import java.util.Map;

public final class OmniPillarTextureType extends SimpleTextureType
{
    private static final Direction[] DIR_AXIS_Y = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    private static final Direction[] DIR_AXIS_X = new Direction[] { Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH };
    private static final ConnectionDirection[] CONDIR_AXIS_Y = new ConnectionDirection[] {
            ConnectionDirection.UP, ConnectionDirection.UP, ConnectionDirection.UP, ConnectionDirection.UP
    };
    private static final ConnectionDirection[] CONDIR_AXIS_X = new ConnectionDirection[] {
            ConnectionDirection.LEFT, ConnectionDirection.LEFT, ConnectionDirection.LEFT, ConnectionDirection.LEFT
    };

    @Override
    public void postProcessConnections(Map<Direction, ConnectionState> stateMap)
    {
        for (Direction side : DIR_AXIS_Y)
        {
            ConnectionState state = stateMap.get(side);
            if (state.isSet(ConnectionDirection.UP) || state.isSet(ConnectionDirection.DOWN))
            {
                cleanConnections(stateMap, DIR_AXIS_Y, Direction.UP, Direction.DOWN, CONDIR_AXIS_Y);
                return;
            }
        }
        for (Direction side : DIR_AXIS_X)
        {
            ConnectionState state = stateMap.get(side);
            if (state.isSet(ConnectionDirection.LEFT) || state.isSet(ConnectionDirection.RIGHT))
            {
                cleanConnections(stateMap, DIR_AXIS_X, Direction.EAST, Direction.WEST, CONDIR_AXIS_X);
                return;
            }
        }
        // If X and Y have no connections, it can only be Z or none, so no need to check or clean anything up
    }

    private static void cleanConnections(
            Map<Direction, ConnectionState> stateMap,
            Direction[] allowedDirs,
            Direction remOne,
            Direction remTwo,
            ConnectionDirection[] allowedConDirs
    )
    {
        ConnectionState state = stateMap.get(remOne);
        if (state.connections() != 0)
        {
            stateMap.put(remOne, new ConnectionState(state.type(), state.texture(), (byte) 0));
        }
        state = stateMap.get(remTwo);
        if (state.connections() != 0)
        {
            stateMap.put(remTwo, new ConnectionState(state.type(), state.texture(), (byte) 0));
        }

        for (int i = 0; i < 4; i++)
        {
            ConnectionDirection conDir = allowedConDirs[i];
            byte connections = (byte) ((0b1 << conDir.ordinal()) | (0b1 << conDir.getOpposite().ordinal()));

            Direction side = allowedDirs[i];
            state = stateMap.get(side);
            byte masked = (byte) (state.connections() & connections);
            if (masked != state.connections())
            {
                stateMap.put(side, new ConnectionState(state.type(), state.texture(), masked));
            }
        }
    }

    @Override
    public UV getConnectionUVs(boolean xCon, boolean yCon, boolean diagCon, Direction side)
    {
        if (!xCon && !yCon)
        {
            return PillarTextureType.UV_XY;
        }
        return yCon ? PillarTextureType.UV_Z_TOPBOTTOM : PillarTextureType.UV_Z_SIDE;
    }
}
