package xfacthd.contex.client.type;

import net.minecraft.core.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.state.ConnectionDirection;
import xfacthd.contex.api.type.*;

public sealed class FullTextureType extends DefaultTextureType permits SimpleTextureType, FullCarpetTextureType
{
    private static final ConnectionDirection[] DIRECTIONS = ConnectionDirection.values();
    private static final UV UV_NONE = new UV(0F, 0F, 1F, 1F);
    private static final UV UV_FULL = new UV(0F, 0F, .5F, .5F);
    private static final UV UV_CARDINAL = new UV(.5F, .5F, 1F, 1F);
    private static final UV UV_X_ONLY = new UV(0F, .5F, .5F, 1F);
    private static final UV UV_Y_ONLY = new UV(.5F, 0F, 1F, .5F);
    public static final FullTextureType INSTANCE = new FullTextureType();

    protected FullTextureType() { }

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
        for (ConnectionDirection dir : DIRECTIONS)
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
    public UV getConnectionUVs(boolean xCon, boolean yCon, boolean diagCon, Direction side)
    {
        if (xCon && yCon && diagCon)
        {
            return UV_FULL;
        }
        else if (xCon && yCon)
        {
            return UV_CARDINAL;
        }
        else if (xCon)
        {
            return UV_X_ONLY;
        }
        else if (yCon)
        {
            return UV_Y_ONLY;
        }
        return UV_NONE;
    }
}
