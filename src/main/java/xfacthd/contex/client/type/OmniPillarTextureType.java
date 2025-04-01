package xfacthd.contex.client.type;

import net.minecraft.core.Direction;
import xfacthd.contex.api.state.*;
import xfacthd.contex.api.type.UV;

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
    public static final OmniPillarTextureType INSTANCE = new OmniPillarTextureType();

    private OmniPillarTextureType() { }

    @Override
    public void postProcessConnections(byte[] stateMap)
    {
        for (Direction side : DIR_AXIS_Y)
        {
            byte state = stateMap[side.ordinal()];
            if (isSet(state, ConnectionDirection.UP) || isSet(state, ConnectionDirection.DOWN))
            {
                cleanConnections(stateMap, DIR_AXIS_Y, Direction.UP, Direction.DOWN, CONDIR_AXIS_Y);
                return;
            }
        }
        for (Direction side : DIR_AXIS_X)
        {
            byte state = stateMap[side.ordinal()];
            if (isSet(state, ConnectionDirection.LEFT) || isSet(state, ConnectionDirection.RIGHT))
            {
                cleanConnections(stateMap, DIR_AXIS_X, Direction.EAST, Direction.WEST, CONDIR_AXIS_X);
                return;
            }
        }
        // If X and Y have no connections, it can only be Z or none, so no need to check or clean anything up
    }

    private static void cleanConnections(
            byte[] stateMap,
            Direction[] allowedDirs,
            Direction remOne,
            Direction remTwo,
            ConnectionDirection[] allowedConDirs
    )
    {
        stateMap[remOne.ordinal()] = 0;
        stateMap[remTwo.ordinal()] = 0;

        for (int i = 0; i < 4; i++)
        {
            ConnectionDirection conDir = allowedConDirs[i];
            byte connections = (byte) ((0b1 << conDir.ordinal()) | (0b1 << conDir.getOpposite().ordinal()));

            Direction side = allowedDirs[i];
            byte state = stateMap[side.ordinal()];
            stateMap[side.ordinal()] = (byte) (state & connections);
        }
    }

    @Override
    public UV getConnectionUVs(boolean xCon, boolean yCon, boolean diagCon, Direction side)
    {
        if (!xCon && !yCon)
        {
            return PillarTextureType.UV_NO_CON;
        }
        return yCon ? PillarTextureType.UV_Z_TOPBOTTOM : PillarTextureType.UV_Z_SIDE;
    }
}
