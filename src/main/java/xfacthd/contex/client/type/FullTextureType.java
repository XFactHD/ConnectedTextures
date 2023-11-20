package xfacthd.contex.client.type;

import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.state.ConnectionDirection;
import xfacthd.contex.api.state.ConnectionState;
import xfacthd.contex.api.type.*;

public sealed class FullTextureType extends DefaultTextureType permits SimpleTextureType
{
    private static final ConnectionDirection[] DIRECTIONS = ConnectionDirection.values();
    private static final UV UV_NONE = new UV(0F, 0F, 1F, 1F);
    private static final UV UV_FULL = new UV(0F, 0F, .5F, .5F);
    private static final UV UV_CARDINAL = new UV(.5F, .5F, 1F, 1F);
    private static final UV UV_X_ONLY = new UV(0F, .5F, .5F, 1F);
    private static final UV UV_Y_ONLY = new UV(.5F, 0F, 1F, .5F);

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
        for (ConnectionDirection dir : DIRECTIONS)
        {
            BlockPos otherPos = pos.offset(dir.getOffset(side));
            if (!predicate.test(level, pos, otherPos, state, dir, dir, side, side))
            {
                continue;
            }

            BlockPos otherPosRel = otherPos.relative(side);
            if (!predicate.test(level, pos, otherPosRel, state, dir, dir.mapToOppositeFace(side), side, side.getOpposite()))
            {
                connections |= (byte) (1 << dir.ordinal());
            }
        }
        return new ConnectionState(this, texture, connections);
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
